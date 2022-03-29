/// <reference types="cypress" />

describe("User Management", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('user-management-data/user-management-action-data').then(function (data) {
      testData = data;
      return testData;
    });
  })

  it('Action except edit', () => {
    cy.visit("/");

    cy.get("input[type=email]").type(testData.email);
    cy.get("input[type=password]").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);
    // click on User Management
    cy.get('.ng-star-inserted > .action-text').click();

    cy.wait(2000);

    let row = testData.select_row + 1;

    function next_page() {
      // no of time click for next page
      for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
        cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
          .click({force: true})
      }

    }

    next_page();

    if(testData.flag_action==="Deactivate")
    {
      // click on deactive
      cy.get(':nth-child('+row+') > .action-container > :nth-child(2)').click();
    }
    else if(testData.flag_action==="Activate")
    {
      // click on active
      cy.get(':nth-child('+row+') > .action-container > .ng-star-inserted').click();
    }
    else if(testData.flag_action==="Change Password")
    {

      // click on change password
      cy.get(':nth-child('+row+') > .action-container > :nth-child(3)').click();
      cy.get('.mat-bottom-sheet-container').should('be.visible');

      cy.get("input[formcontrolname=password]").type(testData.new_password);

      cy.get("input[formcontrolname=confirmPassword]").type(testData.new_password);

      cy.get('button').contains('Update Password').click({force:true});
    }

    else if(testData.flag_action=="Disable")
    {
      // click on disable
      cy.get(':nth-child('+row+') > .action-container > :nth-child(4)').click();
    }

  });



})
