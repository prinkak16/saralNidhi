/// <reference types="cypress" />

describe("state wise filer", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('other-activities-data/statewise-filter-on-transactions').then(function (data) {
      testData = data;
      return testData;
    });
    // cy.visit('/');
  })


  it('login and state wise filter ',{ retries: { runMode: 2, openMode: 1,},}, () => {
    cy.visit("/");
    cy.get("input[type=email").type(testData.email);
    cy.get("input[type=password").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);
    cy.get('.count').click();


    // cy.get('#mat-input-3').type(testData.pan_no);
    // cy.wait(3000);
    // cy.get('.mat-warn').click({delay:200});

    cy.wait(2000);
    // click sate input before selecting 
    cy.get('.ng-input > input').click({force: true});
    // Enter stateName
    cy.get('.ng-option-label').contains(testData.stateName).click({force: true});

    // for click [Clear,Search,Download] button
    cy.get('button').contains(testData.click_button_type).click({force: true});

  });
});


