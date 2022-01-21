/// <reference types="cypress" />

describe("Receipt downloading", () => {
  let testData;

  beforeEach(function () {
    cy.fixture('receipt-downloaded-data/receipt-downloading-data').then(function (data) {
      testData = data;
      return testData;
    });

    cy.exec('rm cypress/downloads/*', {log: true, failOnNonZeroExit: false});
  })

  it('test on downloading receipt', () => {
    cy.visit("/");
    cy.get("input[type=email]").type(testData.email);
    cy.get("input[type=password]").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);

    // click on total transaction count
    cy.get('.count').click();

    const downloadsFolder = Cypress.config('downloadsFolder');

    //  cy.task('countFiles', 'cypress/downloads').then((file) => {
    //      cy.log('download file ::'+file);
    //      vk=file;
    //  });

    let file_for_content = "";

    // if specific transaction type required to click
    if (testData.flag_specific_transaction != 0) {
      // click on transaction type
      cy.get('.mat-tab-label').contains(testData.transaction_type).click({force: true});
      cy.wait(1000);
    }

    // no of time click for next page
    for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
      cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
        .click({force: true})
    }

    cy.wait(2000);
    let row = testData.select_row+1;
    // select row
    cy.get(':nth-child(' + row + ') > .btn-separate > .mat-icon').click();

    cy.wait(2000);
    cy.wait(1000).then(() => {
      cy.task('countFiles', 'cypress/downloads').then((file) => {
        cy.log('download file ::' + file);
        file_for_content = file;

        if (file_for_content != '') {
          cy.log("file after download :" + file_for_content);
          cy.task('isExistPDF', file_for_content).should('equal', true);
        } else {
          cy.wrap(1).should('eq', 2, {message: 'file not found or not downloaded yet...'});
        }
      })
    })

  });

});
