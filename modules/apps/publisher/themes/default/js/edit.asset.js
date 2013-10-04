/*
 Description: The script is used to edit an asset
 Filename: edit.asset.js
 Created Date: 17/8/2013
 */
$(function () {

    //The container used to display messages to the user
    var MSG_CONTAINER='#msg-container-recent-activity';
    var ERROR_CSS='alert alert-error';
    var SUCCESS_CSS='alert alert-info';
    
    /* creating a new version */
    //The existing versions of the asset
    var existingVersionList=null;
    var ASSET_VERSION_CONTAINER='#asset-new-version-control-group';
    var ASSET_NEW_VERSION_MSG_CONTAINER='#asset-new-version-msgs';
    var CSS_ERR='control-group error';
    var CSS_SUCCESS='control-group success';

    $('#editAssetButton').on('click', function () {
        var data = {};

        //Obtain the current url
        var url=window.location.pathname;

        //The type of asset
        var type=$('#meta-asset-type').val();


        //The id
        //Break the url into components
        var comps=url.split('/');

        //Given a url of the form /pub/api/asset/{asset-type}/{asset-id}
        //length=5
        //then: length-2 = {asset-type} length-1 = {asset-id}
        var id=comps[comps.length-1];

        //Extract the fields
        var fields = $('#form-asset-edit :input');

        //Create the data object which will be sent to the server
        fields.each(function () {

            if ((this.type != 'button')&&(this.type!='reset')&&(this.type!='hidden')) {
                data[this.id] = this.value;
            }
        });

        var url='/publisher/api/asset/'+type+'/'+id;

        //Make an AJAX call to edit the asset
        $.ajax({
            url:url,
            type:'PUT',
            data:JSON.stringify(data),
            contentType:'application/json; charset=utf-8',
            dataType:'json',
            success:function(response){
                createMessage(MSG_CONTAINER,SUCCESS_CSS,'Asset updated successfully');
            },
            error:function(response){
                createMessage(MSG_CONTAINER,ERROR_CSS,'Asset was not updated successfully.');
            }
        })

    });


    /*
    The function creates a message and displays it in the provided container element.
    @containerElement: The html element within which the message will be displayed
    @cssClass: The type of message to be displayed
    @msg: The message to be displayed
     */
    function createMessage(containerElement,cssClass,msg){
        var date=new Date();
        var time=date.getDate()+'/'+(date.getMonth()+1)+'/'+date.getFullYear()+' '+date.getHours()+':'+date.getMinutes()
        +':'+date.getSeconds();
        var infoMessage='<div class="'+cssClass+'">'
           +'<a data-dismiss="alert" class="close">x</a>'
           +time+' '+msg+'</div';

        //Place the message
        $(containerElement).html(infoMessage);
    }
    
    
  

    //initCssContainer(ASSET_VERSION_CONTAINER,[CSS_ERR,CSS_SUCCESS]);

    $('#btn-increase-version-asset').on('click', function (e) {
    	e.preventDefault();
    	
        var MSG_ERR_NO_VERSION='Please enter a version number.';

        var url=window.location.pathname;

        //Obtain the asset id
        var assetId=getAssetIdFromUrl(url);

        var assetType=$('#meta-asset-type').val();

        //Obtain the version entered by the user
        var userProvidedVersion = $('#asset-new-version').val();

        //Check if the user has entered a version
        if(!userProvidedVersion){
            //alert(MSG_ERR_NO_VERSION);
            displayVersionMessage({msgCss:CSS_ERR,message:MSG_ERR_NO_VERSION});
            return;
        }

        displayVersionMessage({msgCss:CSS_SUCCESS,message:'Checking version..'});

        //alert('id: '+assetId+' version: '+userProvidedVersion);

        var path='/publisher/api/version/'+assetType+'/'+assetId;

        //Make a call an obtain the existing asset versions
        $.ajax({
            url:path,
            type:'GET',
            success:function(response){
              // alert(response);

               var versionList=JSON.parse(response);

               //Check if the version entered by the user is an existing one
               var existingVersion=checkIfExisting(versionList,userProvidedVersion);

               //Display a message to the user indicating the version exists
               if(existingVersion){
                 displayVersionMessage({msgCss:CSS_ERR,message:'The version you entered: '+userProvidedVersion+' already exists.'});
                 return;
               }

                displayVersionMessage({msgCss:CSS_SUCCESS,message:'Creating new version..'});

                createNewVersion(userProvidedVersion,assetId,assetType);
            },
            error:function(){
                alert('unable to retrieve asset version details.');
            }
        });

    });

    /*
    The function is used to check if the version entered by the user exists
    @versionList: A list of versions
    @newVersion: The new version entered by the user
    @return: True if the version exists,else False
     */
    function checkIfExisting(versionList,newVersion){
        var found=false;

        for(var index=0;(index<versionList.length)&&(!found);index++){
            if(versionList[index].version==newVersion){
                  found=true;
            }
        }

        return found;
    }

    /*
    The function is used to create a new version of a given asset
    @assetId: The id of the asset to be created
    @newVersion: The new version of the asset to be created
     */
    function createNewVersion(newVersion,assetId,assetType){
        var path='/publisher/api/version/'+assetType+'/'+assetId+'/'+newVersion;


        $.ajax({
            url:path,
            type:'POST',
            success:function(response){
				$('#modal-redirect').modal('show');
				setTimeout(function(){
					 var newVersionDetails=JSON.parse(response);
                	 window.location=newVersionDetails.url;
				},2000);
               
            },
            error:function(){
                displayVersionMessage({msgCss:CSS_ERR,message:'A new version of this '+assetType+' was not created.'})
            }
        })
    }

    /*
    The function is used to display a message in-line
    @msg: The message to be displayed
     */
    function displayVersionMessage(msg){
        var container=ASSET_VERSION_CONTAINER;
        var containerCss=msg.msgCss;
        var msgContainer=ASSET_NEW_VERSION_MSG_CONTAINER;
        var message=msg.message;

        $(container).attr('class',containerCss);
        //$(container).toggleClass(containerCss);
        $(msgContainer).html(message);
    }

    /*
    The function is used to initialize the container housing the version
    input
    @container: The container within which the version details reside
    @classes: The CSS classes to be added to the container.
     */
    function initCssContainer(container,classes){
       /* for(var index in classes){
            console.log('container: '+container+' added class: '+classes[index]);

            $(container).addClass(classes[index]);
            $(container).toggleClass(classes[index]);
        }  */

    }

    /*
     The function is used to parse a url to obtain the
     asset id and type.
     @return: The id component of the url
     */
    function getAssetIdFromUrl(url) {
        //The id
        //Break the url into components
        var comps = url.split('/');

        //Given a url of the form /pub/api/asset/{asset-type}/{asset-id}
        //length=5
        //then: length-2 = {asset-type} length-1 = {asset-id}
        var id = comps[comps.length - 1];

        return id;
    }


});
