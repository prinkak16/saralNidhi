var electronInstaller = require('electron-winstaller');

resultPromise = electronInstaller.createWindowsInstaller({
  appDirectory: './release/MyAwesomeApp-win32-x64',
  outputDirectory: './release/installer',
  authors: 'Me',
  exe: 'SaralApp.exe'
});

resultPromise.then(() => console.log("It worked!"), (e) => console.log(`No dice: ${e.message}`));
