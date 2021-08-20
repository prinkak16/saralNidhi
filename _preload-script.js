(function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
    const ipcRenderer = require('electron').ipcRenderer;
    const remote = require('electron').remote;
    const downloadItem = require('./templete.js');
    const { shell } = require('electron')
    const dialog = require('electron').dialog
    ipcRenderer.on('downloadInProgress', (event, args) => {
      progressHandler(event, args);
      const close = document.getElementById(`${args.id}-close`);
      const main = document.getElementById(`${args.id}-main`);
      if (close) {
        close.addEventListener('click', () => {
          main.remove();
        });
      }

    });

    ipcRenderer.on('downloadCompleted', (event, args) => {
      const completedItem = document.getElementById(`${args.id}`);
      const bar = document.getElementById(`${args.id}-bar`);
      const folder = document.getElementById(`${args.id}-folder`);
      const close = document.getElementById(`${args.id}-close`);
      const action = document.getElementById(`${args.id}-actions`);
      const main = document.getElementById(`${args.id}-main`);
      const per = document.getElementById(`${args.id}-per`);
      const file = document.getElementById(`${args.id}-file`);

      if (action) {
        action.classList.remove('hidden');
      }

      if (per) {
        per.classList.add('hidden');
      }

      if (completedItem) {
        completedItem.innerText = 'Completed';
      }

      if (folder) {
        folder.addEventListener('click', () => {
          const showResponse = shell.showItemInFolder(args.path);
          if (showResponse) {
            remote.dialog.showErrorBox('File found', `File not found in path${args.path}`);
          } else {
            remote.dialog.showErrorBox('File not found', `File not found in path${args.path}`);
          }
        });
      }

      if (file) {
        file.addEventListener('click', () => {
          const showFile = shell.openPath(args.path);
          if (showFile) {
            remote.dialog.showErrorBox('File found', `File not found in path${args.path}`);
          } else {
            remote.dialog.showErrorBox('File not found', `File not found in path${args.path}`);
          }
        });
      }


      if (close) {
        close.addEventListener('click', () => {
          main.remove();
        });
      }

      if (bar) {
        bar.innerHTML = null;
      }

    });

    ipcRenderer.on('downloadStated', (event, args) => {
      createDownloadDOM(event, args);
    });

    function progressHandler(e, args) {
      const progressBar = document.getElementById(`${args.id}-progress`);
      const per = document.getElementById(`${args.id}-per`);
      if (progressBar) {
        progressBar.max = args.itemTotal;
        progressBar.value = args.received;
        per.innerText = Math.round((args.received / args.itemTotal) * 100) + '%';
      }
    }

    function createDownloadDOM(e, args) {
      const mainDOM = document.getElementById('main');

      if (mainDOM) {
        mainDOM.innerHTML += downloadItem(args);
      }
    }

  },{"./templete.js":2,"electron":"electron"}],2:[function(require,module,exports){
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
        <span class="text-white ">${itemName}</span>
        <span class="float-right" ><i class="fa fa-times text-white p-2" aria-hidden="true" id="${id}-close" title="Close popup"></i></span> <br>
        <span class="ml-2 text-white">${fileSize}</span>
      </div>
      <div class="download-bar-progress" id="${id}-bar">
      <progress id="${id}-progress" class="progress-bar"></progress>
      </div>
      <div id="${id}" class="dowload-bar-footer ml-2 mt-1"></div>
      <div id="${id}-per" class="dowload-bar-footer ml-2">0%</div>
        <div id="${id}-actions" class="hidden">
        <span id="${id}-file"><i class="fa fa-eye text-white ml-2" aria-hidden="true" title="Open in file"></i></span>
         <span id="${id}-folder"><i class="fa fa-folder text-white ml-2" aria-hidden="true" title=" Open in folder"></i></span>
       </div>
        <div class="clearfix"></div>
        </div>`);
    }

    module.exports = generateTemplete;

  },{}]},{},[1]);
