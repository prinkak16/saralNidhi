/// <reference types="cypress" />

describe("Pan and Receipt", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('other-activities-data/search-pan-then-receipt').then(function (data) {
      testData = data;
      return testData;
    });
    // cy.visit('/');
  })


  it('login, search pan and print the receipt', () => {
    cy.visit("/");
    cy.get("input[type=email]").type(testData.email);
    cy.get("input[type=password]").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);
    cy.get('.count').click();


    cy.get('input[formcontrolname="query"]').type(testData.pan_no);

    // click on search icon
    cy.get('.mat-warn > .mat-button-wrapper').click({force: true});

    cy.wait(2000);
    // click on Receipt
    cy.get('.mat-warn > .mat-button-wrapper').click({force: true});
    cy.wait(2000);


    // no of time click for next page
    for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
      cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
        .click({force: true})
    }

    cy.wait(2000);

    // select row
    var row = testData.select_row+1;

    cy.get(":nth-child(" + row + ") > .btn-separate > .btn-send > .mat-tooltip-trigger").click({force: true});

    // send Receipt on email by pressing submit

    //cy.get('button').contains('Submit').click();

  });
});
