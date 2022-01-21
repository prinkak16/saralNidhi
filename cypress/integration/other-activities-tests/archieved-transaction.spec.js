/// <reference types="cypress" />

describe("Archieved Transaction", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('other-activities-data/archieve-transaction').then(function (data) {
      testData = data;
      return testData;
    });
  })


  it('login, then Archieved transaction ', () => {
    cy.visit("/");
    cy.get("input[type=email").type(testData.email);
    cy.get("input[type=password").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');

    // click on Archieve transaction
    cy.get('[ng-reflect-router-link="/dashboard/archived_transactio"] > .action-text').click();

    cy.wait(1000);

    // click on transaction type or not
    if(testData.flag_specific_transaction!==0) {

      cy.get('.mat-tab-label-content').contains(testData.transaction_type).click({force:true});
    }

    let row = testData.select_row + 1;
    function row_and_next_page() {
      // no of time click for next page
      for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
        cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
          .click({force: true})
      }
      cy.wait(1000);
      // select row
      cy.get(':nth-child('+row+') > .action-btn > .mat-tooltip-trigger > .mat-icon').click();
    }

    row_and_next_page();

    //click on No/Yes
    cy.get('button').contains(testData.final_operation).click();

  })

});
