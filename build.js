var electronInstaller = require('electron-winstaller');

resultPromise = electronInstaller.createWindowsInstaller({
  appDirectory: 'builds/SaralNidhi-win32-x64',
  outputDirectory: 'installer/',
  authors: 'Jarvis Consulting',
  exe: 'SaralNidhi.exe'
});

resultPromise.then(() => console.log("It worked!"), (e) => console.log(`No dice: ${e.message}`));
