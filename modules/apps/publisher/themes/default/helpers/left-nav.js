var generateLeftNavJson = function(data) {
    var leftNavItems = { leftNavLinks :
        [
            {
                name : "Browse All",
                additionalClasses : "prominent-link",
                url : "/publisher/assets/" + data.shortName + "/"
            },
            {
                name : "Add " + data.shortName + "",
                iconClass : "icon-plus-sign-alt",
                url : "/publisher/asset/" + data.shortName + ""
            },
            {
                name: "Statistics",
                iconClass: "icon-dashboard",
                url: "/publisher/assets/statistics/" + data.shortName + "/"
            }
        ]
    };
    if(data.artifact){
        leftNavItems = { leftNavLinks :
            [
                {
                    name : "Browse All",
                    additionalClasses : "prominent-link",
                    url : "/publisher/assets/" + data.shortName + "/"
                },
                {
                    name : "Overview",
                    iconClass : "icon-list-alt",
                    url : "/publisher/asset/operations/view/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name : "Edit",
                    iconClass : "icon-edit",
                    url : "/publisher/asset/operations/edit/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name : "Life Cycle",
                    iconClass : "icon-retweet",
                    url : "/publisher/asset/operations/lifecycle/" + data.shortName + "/" + data.artifact.id + ""
                }
            ]
        };
    }
    return leftNavItems;
};