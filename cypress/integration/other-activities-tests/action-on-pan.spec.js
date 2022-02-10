/// <reference types="cypress" />

describe("Pan card section", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('other-activities-data/approve-reject').then(function (data) {
      testData = data;
      return testData;
    });
  })


  it('login then Approve & Reject', () => {
    cy.visit("/");
    cy.get("input[type=email").type(testData.email);
    cy.get("input[type=password").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(1000);
    // click on action required for pan card
    //cy.get('[ng-reflect-router-link="/dashboard/pan_action"] > .action-text').click({force: true});

     cy.get('.action-text').contains('Action Required for Pancard').click({force:true});

    cy.wait(2000);
    let row = testData.select_row + 1;
    function row_and_next_page() {
      // no of time click for next page
      for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
        cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
          .click({force: true})
      }
      cy.wait(1000);
      // select row
      cy.get(':nth-child('+row+') > .cdk-column-action > .cursor-pointer').click({force: true});
    }

    row_and_next_page();
    //cy.get('.mat-menu-content').should('be.visible');
    cy.get('#mat-dialog-0').should('be.visible');

    if(testData.approve!=0) {
      cy.get('input[type="radio"]').check('approve',{force:true});

      cy.get('[formcontrolname="accountant_pan_remarks"]').type(testData.approve_remark);

    }

    if(testData.reject!=0) {
      cy.get('input[type="radio"]').check('reject',{force:true});

      cy.get('[formcontrolname="accountant_pan_remarks"]').type(testData.reject_remark);

    }
    // Submit/Close button
    cy.get('button').contains(testData.final_operation).click({force: true});

    cy.wait(1000);
    //download csv file
    //cy.get('.downloadButtonCSV').click({force:true});



  })

});
