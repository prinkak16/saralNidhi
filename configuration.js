const fs = require('fs')

console.log("Starting")
let originalFile = "./config.json"
let envFile = null;
if (process.env.NODE_ENV === 'staging') {
  envFile = "./config.staging.json"
} else if (process.env.NODE_ENV === 'prod') {
  envFile = "./config.prod.json"
}

if (!envFile) {
  console.log("ENV not found so existing")
  return 0;
}
let packageJson = JSON.parse(fs.readFileSync("./package.json", 'utf8'));
//increasing version

console.log("changing version from : " + packageJson.version)
versionArray = packageJson.version.split(".")

if (versionArray.length === 3){
  versionArray[2] = Math.floor(Math.random()*90000) + 10000;
  packageJson.version = versionArray.join(".")
  console.log("New  version  : " + packageJson.version)
  fs.writeFileSync("./package.json", JSON.stringify(packageJson, null, 2));
}

titleContent = fs.readFileSync("./src/index.html", 'utf8').toString()
let datetime = new Date();

titleContent = titleContent.replace(/<title>[\s\S]*?<\/title>/, '<title>' + "Nidhi Collection " + datetime.toDateString() + " - " + datetime.toLocaleTimeString()  + '<\/title>');
fs.writeFileSync("./src/index.html", titleContent)



let envObj = JSON.parse(fs.readFileSync(envFile, 'utf8'));
let originalJson = JSON.parse(fs.readFileSync(originalFile, 'utf8'))

console.log(envObj)
console.log(originalJson)


Object.keys(envObj).forEach(key => {
  originalJson[key] = envObj[key]
})
console.log("Writing " + process.env.NODE_ENV + " configuration to conf file")
fs.writeFileSync(originalFile, JSON.stringify(originalJson, null, 2));
console.log("Done.")
