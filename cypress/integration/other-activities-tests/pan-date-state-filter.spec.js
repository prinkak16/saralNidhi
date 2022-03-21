/// <reference types="cypress" />

describe("Pan and date", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('other-activities-data/pan-date-state').then(function (data) {
      testData = data;
      return testData;
    });
    // cy.visit('/');
  })


  it('search pan then filter with date range and state', () => {
    cy.visit("/");
    cy.get("input[type=email").type(testData.email);
    cy.get("input[type=password").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);
    cy.get('.count').click();

    // type pan_no
    cy.get('#mat-input-3').type(testData.pan_no);

    cy.wait(2000);
    cy.get('.mat-datepicker-toggle-default-icon > path').click({force: true});

    // click prev calender button no of times..
    for (let i = 0; i < testData.no_of_times_prev_calender_btn_click; i++) {
      cy.get('.mat-calendar-previous-button').click({force: true});
    }

    cy.get('.mat-calendar-content').contains(testData.first_date_digit).click({force: true});
    cy.wait(2000);

    // click next calender button no of times..
    for (let i = 0; i < testData.no_of_times_next_calender_btn_click; i++) {
      cy.get('.mat-calendar-next-button').click({force: true});
    }

    cy.get('.mat-calendar-content').contains(testData.second_date_digit).click({force: true});

    //cy.get('#mat-select-value-1').click({force: true});
    cy.get('.ng-input > input').click().get('.ng-option-label').contains(testData.stateName).click({force: true});
    

    // for click [Clear,Search,Download] button
    cy.get('button').contains(testData.click_button_type).click({force: true});
  });
});
