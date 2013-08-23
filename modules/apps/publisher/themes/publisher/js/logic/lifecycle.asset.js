$(function(){

    var url=window.location.pathname;
    var comps=url.split('/');

    var id=comps[comps.length-1];
    var asset=comps[comps.length-2];

    console.log(asset);
    console.log(id);

    $('#btn-asset-promote').on('click',function(){
       console.log('/publisher/api/lifecycle/Promote/'+asset+'/'+id);
        $.ajax({
          url:'/publisher/api/lifecycle/Promote/'+asset+'/'+id,
          type:'PUT',
          success:function(response){
              alert('Promoted');


              $.ajax({
                  url:'/publisher/api/lifecycle/'+asset+'/'+id,
                  type:'GET',
                  success:function(response){
                      $('#state').html(response);
                      $('#view-lifecyclestate').html(response);
                      BuildCheckList(asset,id);
                  },
                  error:function(response){
                      $('#state').html('Error obtaining state');
                  }
              });
          },
          error:function(response){
              alert('Not promoted');
          }
        });
    });

    $('#btn-asset-demote').on('click',function(){
        $.ajax({
            url:'/publisher/api/lifecycle/Demote/'+asset+'/'+id,
            type:'PUT',
            success:function(response){
                alert('Demoted');

                $.ajax({
                    url:'/publisher/api/lifecycle/'+asset+'/'+id,
                    type:'GET',
                    success:function(response){
                        $('#state').html(response);
                        $('#view-lifecyclestate').html(response);
                        BuildCheckList(asset,id);
                    },
                    error:function(response){
                        $('#state').html('Error obtaining state');
                    }
                });
            },
            error:function(response){
                alert('Not demoted');
            }
        });


    });

    BuildCheckList(asset,id);

    function BuildCheckList(asset,id){
        $('#checklist').html('');
        //Make a call to the lifecycle check list
        $.ajax({
            url:'/publisher/api/lifecycle/checklist/'+asset+'/'+id,
            type:'GET',
            success:function(response){
                var out='<ul>';
                console.log(response);
                var obj=JSON.parse(response);
                console.log(obj.status);
                for(var index in obj.checkListItemNames)
                {
                   var current=obj.checkListItemNames[index];
                   console.log(current);
                   out+='<li><input type="checkbox">'+current+'</label></li>';
                }
                out+='</ul>';
                $('#checklist').html(out);
            }

        });
    }




});