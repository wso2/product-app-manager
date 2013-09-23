var schema=function(){
  return{
     name:'permission',
     fields:[
         {
             name:'uuid'
         },
         {
             name:'bucket'
         },
         {
             name:'read'
         },
         {
             name:'write'
         },
         {
             name:'delete'
         }

     ]
  };
};
