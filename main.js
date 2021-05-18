'use strict';
const {app, BrowserWindow} = require('electron')
const url = require("url");
const path = require("path");
const electron = require("electron");
const guid = require('./uid');
require('electron-dl')();

let mainWindow
const preloadScript = path.join(__dirname, '_preload-script.js');

function createWindow () {
  mainWindow = new BrowserWindow({
    width: 1024,
    height: 768,
    minWidth:1024,
    webPreferences: {
      nodeIntegration: true,
      preload: preloadScript
    }
  })

  mainWindow.loadURL(
    url.format({
      pathname: path.join(__dirname, `/dist/angular-electron/index.html`),
      protocol: "file:",
      slashes: true
    })
  );
  // Open the DevTools.
  mainWindow.webContents.openDevTools()

  mainWindow.on('closed', function () {
    mainWindow = null
  })

  // download function
  mainWindow.webContents.session.on('will-download', (event, item, webContents) => {
    const browserWindow = electron.BrowserWindow.fromWebContents(webContents);
    item.id = guid(); // can be done differently
    browserWindow.webContents.send('downloadStated', {
      itemTotal: item.getTotalBytes(),
      received: item.getReceivedBytes(),
      name: item.getFilename(),
      path: item.getSavePath(),
      id: item.id
    });
    item.on('updated', (event, state) => {
      if (browserWindow.isDestroyed()) {
        return;
      }
      if (state === 'interrupted') {
        // Interrupted
      } else if (state === 'progressing') {
        if (item.isPaused()) {
          // Handle pause
        } else {
          browserWindow.webContents.send('downloadInProgress', {
            itemTotal: item.getTotalBytes(),
            received: item.getReceivedBytes(),
            name: item.getFilename(),
            path: item.getSavePath(),
            id: item.id
          });
        }
      }
    });
    item.once('done', (event, state) => {
      if (browserWindow.isDestroyed()) {
        return;
      }
      if (state === 'completed') {
        browserWindow.webContents.send('downloadCompleted', {
          itemTotal: item.getTotalBytes(),
          received: item.getReceivedBytes(),
          name: item.getFilename(),
          path: item.getSavePath(),
          id: item.id
        });
      } else {
        // Handle
      }
    });
  });

  return mainWindow;

}

app.on('ready', createWindow)

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})

app.on('activate', function () {
  if (mainWindow === null) createWindow()
})
