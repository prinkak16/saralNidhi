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
    
     cy.get('.action-text').contains('Action Required for Pancard').click({force:true});

    cy.wait(2000);
    let row = testData.select_row + 1;
    //function row_and_next_page() {

      // no of time click for next page
      for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
        cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
          .click({force: true})
      }

      cy.wait(1000);
      let status_value;
      // select row and check if status is invalid then click on approvre/reject 
       cy.get(':nth-child('+row+') > .cdk-column-status').then(($status) => {
        const txt1 = $status.text();
        status_value=txt1;
        cy.log("txt1 :"+txt1);
    });

      cy.wait(1000).then(()=> {
        if(status_value=="Invalid") {
          cy.log("**status_value**"+status_value);
          // approvre/reject action button
          cy.get(':nth-child('+row+') > .cdk-column-action > .mat-tooltip-trigger').should('exist').click({force: true});
          cy.get('#mat-dialog-0').should('be.visible');

          if(testData.flag_approve_reject!=0) {
            cy.log("**Approve/Reject**");
      
            cy.get('[formcontrolname="accountant_pan_remarks"]').type(testData.remark);
      
           if(testData.action_type=="Approve") {
            cy.get('.approve_btn').click({force:true});
           }
           else {
            cy.get('.reject_btn').click({force:true});
           }
      
          }

        }
        else {
          cy.log("**status is Already Approved or Rejected**");
        }
      })
  })

});
