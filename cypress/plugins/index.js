/// <reference types="cypress" />
// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

/**
 * @type {Cypress.PluginConfig}
 */

const fs = require('fs');
const path = require('path')
const pdf = require('pdf-parse');
//const globby = require('globby');

//const repoRoot = path.join(__dirname, '..', '..') ;
const repoRoot = path.join('cypress', 'downloads');
//const repoRoot = path.join('cypress') ;

const downloadDirectory = path.join(__dirname, '..', 'downloads');


const parsePdf = async (pdfName) => {
  const pdfPathname = path.join(repoRoot, pdfName)
  let dataBuffer = fs.readFileSync(pdfPathname);
  return await pdf(dataBuffer)  // use async/await since pdf returns a promise
}

//--------------------------

const findPDF = (PDFfilename) => {
  const PDFFileName = `${downloadDirectory}/${PDFfilename}`;
  const contents = fs.existsSync(PDFFileName);
  return contents;
};

const hasPDF = (PDFfilename, ms) => {
  const delay = 10;
  return new Promise((resolve, reject) => {
    if (ms < 0) {
      return reject(
        new Error(`Could not find PDF ${downloadDirectory}/${PDFfilename}`)
      );
    }
    const found = findPDF(PDFfilename);
    if (found) {
      return resolve(true);
    }
    setTimeout(() => {
      hasPDF(PDFfilename, ms - delay).then(resolve, reject);
    }, 10);
  });
};


module.exports = (on, config) => {

  on('task', {
    countFiles(folderName) {
      return new Promise((resolve, reject) => {
        fs.readdir(folderName, (err, files) => {
          if (err) {
            return reject(err)
          }

          //resolve(files.length)
          resolve(files)
        })
      })
    },

    getPdfContent(pdfName) {
      return parsePdf(pdfName)
    },

    isExistPDF(PDFfilename, ms = 4000) {
      console.log(
        `looking for PDF file in ${downloadDirectory}`,
        PDFfilename,
        ms
      );
      return hasPDF(PDFfilename, ms);
    },

  })


}








