/**
 * Description: The script is used to manage the rendering and generation of form data
 * Filename: form-manager.js
 * Created Date: 16/2/2014
 */
var FormManager = {};
var PageFormContainer = {};

$(function () {

    var FORM_MANAGER_DIRECTIVE = '.fm-managed';

    var USE_PLUGINS = 'usePlugins';
    var PLUGIN_ACTION_VALIDATE = 'validate';
    var PLUGIN_ACTION_ISHANDLED = 'isHandled';
    var PLUGIN_ACTION_GET_DATA = 'getData';
    var PLUGIN_ACTION_INIT = 'init';

    var globalPlugins = {};

    function Manager(formId) {
        this.formId = formId;
        this.pluginMap = globalPlugins; //The list of all the registered plugins
        this.fieldMap = {};
        this.formMap = {};

        //Build a map of the fields that will be managed
        buildFieldMap(this.formId, this.fieldMap);

        buildFormMap(this.formId, this.formMap, this.fieldMap);
    }

    /**
     * The function allows a plugin to register itself with the
     * Manager
     * @param plugin The plug-in to be registered
     */
    var register = function (name, plugin) {
        globalPlugins[name] = plugin;
    };

    /**
     * The method will go through the form and each field and then instantiate the
     * plugins that will be used and give a reference
     */
    Manager.prototype.init = function () {

        initElement(this.formMap, this.pluginMap);

        //Initialize each field managed by the form manager
        for (var index in this.fieldMap) {
            initElement(this.fieldMap[index], this.pluginMap);
        }
    };

    /**
     * The method invokes any validation plugins on the form and the fields.A validation
     * plugin is determined by the presence of the validate method in a plugin instance
     */
    Manager.prototype.validate = function () {
        var validationReport = {};
        validationReport.form = {};
        validationReport.failed = false;
        validationReport.form.fields = {};
        validationReport.form.errors = invokePluginAction(this.formMap, PLUGIN_ACTION_VALIDATE);
        var validationErrors = [];

        //Attempt to validate all fields
        for (var fieldName in this.fieldMap) {
            validationErrors = invokePluginAction(this.fieldMap[fieldName], PLUGIN_ACTION_VALIDATE);

            //Only add to the report if there is a message
            if (validationErrors.length > 0) {
                validationReport.failed = true;
                validationReport.form.fields[fieldName] = validationErrors;
            }
        }

        return validationReport;
    };

    /**
     * The method is used to extract data from all of the fields in the form into one object
     */
    Manager.prototype.getData = function () {
        var data = {};

        // var result = invokePluginAction(this.formMap, PLUGIN_ACTION_GET_DATA,data);
        // data = formatData(data, result);

        for (var fieldName in this.fieldMap) {
            result = invokePluginAction(this.fieldMap[fieldName], PLUGIN_ACTION_GET_DATA, data);
            data = formatData(data, result);
        }

        //Invoke the form level plugins after the field level plugins
        var result = invokePluginAction(this.formMap, PLUGIN_ACTION_GET_DATA, data);
        data = formatData(data, result);

        return data;
    };

    /**
     * The method returns a FormData object containing the data of all managed fields
     * @returns A FormData object containing the information of the fields
     */
    Manager.prototype.getFormData = function () {
        var data = this.getData();
        var formData = new FormData();

        for (var key in data) {
            formData.append(key, data[key]);
        }

        return formData;
    };

    /**
     * The function will allow a dynamic element to be added to the manager
     * @param element The dynamic element to be added
     */
    Manager.prototype.addDynamicElement = function (domElement) {

        var newFieldMap = {};

        $(domElement).find('.fm-managed').each(function () {
            var fieldId = this.id;
            newFieldMap[fieldId] = {};
            newFieldMap[fieldId].id = this.id;
            newFieldMap[fieldId].name = this.name;
            newFieldMap[fieldId].value = this.value || '';
            newFieldMap[fieldId].meta = $('#' + fieldId).data() || {};
            newFieldMap[fieldId].plugins = [];
        });

        console.log(newFieldMap);

        //Initialize the plugins
        for (var index in newFieldMap) {
            initElement(newFieldMap[index], this.pluginMap);
        }

        //Add them to the field Map
        addToFieldMap(newFieldMap,this.fieldMap);

        console.log(this.fieldMap);
    };

    Manager.prototype.removeDynamicElement=function(domElement){
        var fieldsToRemove=[];

        $(domElement).find('.fm-managed').each(function(){
            var fieldId=this.id;
            fieldsToRemove.push(fieldId);
        });

        //Remove all properties for the given field id
        for(var index in fieldsToRemove){
            console.log('Removing field: '+fieldsToRemove[index]);
            delete this.fieldMap[fieldsToRemove[index]];
        }
    };

    var addToFieldMap=function(newFields,fieldMap){
       for(var index in newFields){
           fieldMap[index]=newFields[index];
       }
    };

    /**
     * The function is used to format the data provided
     * @param result
     * @param data
     */
    var formatData = function (result, data) {
        var elements = [];
        var item;

        //Check if the passed data is in the form of an array
        if (data instanceof Array) {
            elements = data;
        }
        else {
            elements.push(data);
        }

        //Go through all elements in the data object
        for (var index in elements) {
            item = elements[index];
            for (var key in item) {
                result[key] = item[key];
            }
        }

        return result;
    }

    /**
     * The function takes an element (field or form) and then creates instances of
     * all required plugins. Each element has its own instance of a plugin
     * @param elementMap An object containing meta information on an element (field or form)
     * @param pluginMap The map of plugins available to the form manager
     */
    var initElement = function (elementMap, pluginMap) {
        var pluginsToUse = elementMap.meta[USE_PLUGINS] || '';
        pluginsToUse = pluginsToUse.split(',');
        var plugins = getPlugins(pluginsToUse, pluginMap);
        var instance;
        var isHandled;

        console.log('Installing plugins for element: ' + elementMap.id);
        for (var index in plugins) {

            instance = new plugins[index]();
            isHandled = true;
            //Check if the plugin can handle the element
            if (instance[PLUGIN_ACTION_ISHANDLED]) {
                isHandled = instance.isHandled(elementMap);
            }

            //Check if the element is handled
            if (isHandled) {
                console.log('Plugin: ' + index + ' applied to element: ' + elementMap.id);
                //Check if there is an init method
                if (instance[PLUGIN_ACTION_INIT]) {
                    instance.init(elementMap);
                    console.log('Plugin: ' + index + ' init method called');
                }

                //Add the plugin instance to the field
                elementMap.plugins.push(instance);
            }
            else {
                console.log('Plugin: ' + index + ' does not support the provided element: ' + elementMap.id);
            }
        }

    };

    /**
     * The function is used to invoke a given plugin action on an element (field or form)
     * If the action is not supported by the plugin then an error is recorded
     * @param elementMap
     * @param action
     */
    var invokePluginAction = function (elementMap, action, data) {
        var plugin;
        var result = {};
        var output = [];

        for (var index in elementMap.plugins) {
            plugin = elementMap.plugins[index];

            if (plugin[action]) {
                console.log('Element: ' + (elementMap.id || 'form') + 'action: ' + action + ' by plugin: ' + index);
                result = plugin[action](elementMap, data);
                if (result) {
                    output.push(result);
                }
            }

        }

        return output;
    };

    /**
     * The function returns all plugins that can be used with the provided field
     * @param field
     * @param plugins  A plugin map
     * @returns An array of plugins that can be used with the field
     */
    var determineApplicablePlugins = function (field, plugins) {

        var applicablePlugins = [];
        var plugin;

        //Go through all the plugins and check if they are compatible with a given field
        for (var key in plugins) {
            plugin = plugins[key];

            if (plugin.isApplicable(field)) {
                applicablePlugins.push(plugin)
            }
        }
        return applicablePlugins;
    };

    /**
     * The method returns references to a set of plugins names
     * @param pluginNames
     * @plugins: The plugin map
     * @return: An array containing references to the plugin implementation
     */
    var getPlugins = function (pluginNames, plugins) {

        var ref = [];
        var name;

        for (var index in pluginNames) {
            name = pluginNames[index];

            if (plugins.hasOwnProperty(name)) {
                ref.push(plugins[name]);
            }

        }

        return ref;
    };


    /**
     * The function obtains all fields that need to be managed
     * by the FormManager by checking for elements containing the
     * fm-manage class
     * @param formId
     */
    var buildFieldMap = function (formId, fieldMap) {
        var fieldId;

        $(FORM_MANAGER_DIRECTIVE).each(function () {
            fieldId = this.id;
            fieldMap[fieldId] = {};
            fieldMap[fieldId].id = this.id;
            fieldMap[fieldId].name = this.name;
            fieldMap[fieldId].value = this.value || '';
            fieldMap[fieldId].meta = $('#' + fieldId).data() || {};
            fieldMap[fieldId].plugins = [];
        });
    };

    /**
     * The function is used to extract form meta data
     * @param formId  The id of the form
     * @param formMap An object used to store meta information and plugin details for a form
     * @param fieldMap An object containing a reference to all the fieldd within the form
     */
    var buildFormMap = function (formId, formMap, fieldMap) {
        formMap.meta = $('#' + formId).data() || {};
        formMap.plugins = [];     //A list of plugins to be applied to the form level
        formMap.fields = fieldMap;
    };

    FormManager = Manager;
    FormManager.register = register;

    var formManagerInstance = null;

    PageFormContainer.setInstance = function (instance) {
        formManagerInstance = instance;
    };

    PageFormContainer.getInstance = function () {
        return formManagerInstance;
    };

}());