/// <reference types="cypress" />

describe("Date Range", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('other-activities-data/date-range-transaction-filter').then(function (data) {
      testData = data;
      return testData;
    });
    // cy.visit('/');
  })


  it('date range transaction filter', () => {
    cy.visit("/");
    cy.get("input[type=email").type(testData.email);
    cy.get("input[type=password").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);
    cy.get('.count').click();


    // cy.get('#mat-input-3').type(testData.pan_no);
    // cy.wait(3000);
    // cy.get('.mat-warn').click({delay:200});

    cy.wait(1500);

    cy.get('.mat-datepicker-toggle-default-icon > path').click({force:true});

    cy.wait(2000);
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


    // for click [Clear,Search,Download] button
    cy.get('button').contains(testData.click_button_type).click({force: true});

    // click next page no of times...
    for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
      cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
        .click({force: true})
    }

  });
});
