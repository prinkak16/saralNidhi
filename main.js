const {app, BrowserWindow} = require('electron')
const config = require("./config.json");
// this should be placed at top of main.js to handle setup events quickly
if (handleSquirrelEvent()) {
  // squirrel event handled and app will exit in 1000ms, so don't do anything else
  return;
}
const isDevelopment = config.development
function handleSquirrelEvent() {
  if (process.argv.length === 1) {
    return false;
  }

  const ChildProcess = require('child_process');
  const path = require('path');

  const appFolder = path.resolve(process.execPath, '..');
  const rootAtomFolder = path.resolve(appFolder, '..');
  const updateDotExe = path.resolve(path.join(rootAtomFolder, 'Update.exe'));
  const exeName = path.basename(process.execPath);

  const spawn = function(command, args) {
    let spawnedProcess, error;

    try {
      spawnedProcess = ChildProcess.spawn(command, args, {detached: true});
    } catch (error) {}

    return spawnedProcess;
  };

  const spawnUpdate = function(args) {
    return spawn(updateDotExe, args);
  };

  const squirrelEvent = process.argv[1];
  switch (squirrelEvent) {
    case '--squirrel-install':
    case '--squirrel-updated':
      // Optionally do things such as:
      // - Add your .exe to the PATH
      // - Write to the registry for things like file associations and
      //   explorer context menus

      // Install desktop and start menu shortcuts
      spawnUpdate(['--createShortcut', exeName]);

      setTimeout(app.quit, 1000);
      return true;

    case '--squirrel-uninstall':
      // Undo anything you did in the --squirrel-install and
      // --squirrel-updated handlers

      // Remove desktop and start menu shortcuts
      spawnUpdate(['--removeShortcut', exeName]);

      setTimeout(app.quit, 1000);
      return true;

    case '--squirrel-obsolete':
      // This is called on the outgoing version of your app before
      // we update to the new version - it's the opposite of
      // --squirrel-updated

      app.quit();
      return true;
  }
}



const path = require('path')
const electron = require('electron');
let squirrelUrl =  config.updateUrl;
const url = require("url");

let mainWindow

function createWindow () {
  mainWindow = new BrowserWindow({
    width: 1024,
    height: 768,
    minWidth:1024,
    webPreferences: {
      nodeIntegration: true
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
  if (isDevelopment){
    mainWindow.webContents.openDevTools()
  }

  mainWindow.on('closed', function () {
    mainWindow = null
  })
}

app.on('ready', createWindow)

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})

app.on('activate', function () {
  if (mainWindow === null) createWindow()
})


const startAutoUpdater = (squirrelUrl) => {
  // The Squirrel application will watch the provided URL
  electron.autoUpdater.setFeedURL(squirrelUrl);

  // Display a success message on successful update
  electron.autoUpdater.addListener("update-downloaded", (event, releaseNotes, releaseName) => {
    electron.dialog.showMessageBox({"message": `The release ${releaseName} has been downloaded. Please click OK and Restart your application.`});
  });

  // Display an error message on update error
  electron.autoUpdater.addListener("error", (error) => {
    electron.dialog.showMessageBox({"message": "Auto updater error: " + error});
  });

  // tell squirrel to check for updates
  electron.autoUpdater.checkForUpdates();
}

app.on('ready', function (){
  if (isDevelopment){
    console.log(config.updateUrl)
  }
  // Add this condition to avoid error when running your application locally
  if (process.env.NODE_ENV !== "dev") startAutoUpdater(squirrelUrl)
});

