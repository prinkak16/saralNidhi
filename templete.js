function generateTemplete(args) {
  const itemName = args.name;
  const id = args.id;
  const units = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  let l = 0, n = parseInt(args.itemTotal, 10) || 0;
  while(n >= 1024 && ++l){
    n = n/1024;
  }
  const fileSize = n.toFixed(n < 10 && l > 0 ? 1 : 0) + ' ' + units[l];

  return (`<div id="${id}-main" class="download-popup">
      <div>
        <span><i class="fa fa-file p-2 file-icon" aria-hidden="true" ></i></span>
        <span class="text-white">${itemName}</span>
        <span class="float-right" ><i class="fa fa-times text-white p-2" aria-hidden="true" id="${id}-close" title="Close popup"></i></span><br>
        <span class="ml-2 text-white">${fileSize}</span>
      </div>
      <div class="download-bar-progress" id="${id}-bar">
      <progress id="${id}-progress" class="progress-bar"></progress>
      </div>
      <div id="${id}" class="dowload-bar-footer ml-2 mt-1"></div>
      <div id="${id}-per" class="dowload-bar-footer ml-2 mt-1">0%</div>
        <div id="${id}-actions" class="hidden">
         <span id="${id}-file"><i class="fa fa-eye text-white ml-2" aria-hidden="true" title="Open in file"></i></span>
         <span id="${id}-folder"><i class="fa fa-folder text-white ml-2" title="Open in folder" aria-hidden="true"></i></span>
       </div>
        <div class="clearfix"></div>
      </div>`);
}

module.exports = generateTemplete;
