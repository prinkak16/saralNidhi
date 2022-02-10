/// <reference types="cypress" />

describe("user-management Edit1", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('user-management-data/edit-user-management-data').then(function (data) {
      testData = data;
      return testData;
    });
    // cy.visit('/');
  })


  it('login then click on Edit option inside user management', () => {
    cy.visit("/");
    cy.get("input[type=email]").type(testData.email);
    cy.get("input[type=password]").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);

    // click on User Management
    cy.get('.ng-star-inserted > .action-text').click({ force: true });

    cy.wait(1000);


    let row = testData.select_row + 1;

    function row_and_next_page() {
      // no of time click for next page
      for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
        cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
          .click({ force: true })
      }
      cy.wait(2000);
      // select row
      cy.get(':nth-child(' + row + ') > .action-container > :nth-child(1)').click({ force: true });
      //cy.get(':nth-child(' + row + ') > .action-container > [ng-reflect-router-link="/dashboard/users/add"]').click({force: true});

    }

    row_and_next_page();

    if (testData.flag_name != 0) {
      cy.get('[formcontrolname="name"]').clear();

      cy.get('[formcontrolname="name"]').type(testData.flag_name_value);
    }

    if (testData.flag_mobile_no != 0) {
      cy.get('[formcontrolname="phone_no"]').clear();

      cy.get('[formcontrolname="phone_no"]').type(testData.flag_mobile_no_value);
    }

    if (testData.flag_email != 0) {
      cy.get('[formcontrolname="email"]').clear();

      cy.get('[formcontrolname="email"]').type(testData.flag_email_value);
    }

    if (testData.flag_role != 0) {
      cy.get('input[type="radio"]').check(testData.role, { force: true });

      cy.get('[formcontrolname="location_ids"]').click({ force: true });

      if (testData.flag_state_zila_mandal != 0) {
        if (testData.role == 'national_accountant' || testData.role == 'state_treasurer' || testData.role == 'state_accountant') {
          let myArray = testData.state_to_be_selected.split(",");
          myArray.forEach(i => {
            cy.get('mat-option > .mat-option-text').contains(i).click({ force: true });
          });
        } else if (testData.role == 'zila_accountant') {
          let myArray = testData.zila_to_be_selected.split(",");
          myArray.forEach(i => {
            cy.get('mat-option > .mat-option-text').contains(i).click({ force: true });
          });
        } else if (testData.role == 'mandal_accountant') {
          let myArray = testData.mandal_to_be_selected.split(",");
          cy.wait(1000);
          myArray.forEach(i => {
            cy.log(i);
            cy.wait(2000);
            cy.get('mat-option > .mat-option-text').contains(i).click({ force: true });
          });
          //cy.get('#mat-option-917 > .mat-option-text').click({force: true});

        }
      }


    }

    // if (testData.flag_state_zila_mandal != 0) {
    //   if (testData.role == 'national_accountant' || testData.role == 'state_treasurer' || testData.role == 'state_accountant') {
    //     let myArray = testData.state_to_be_selected.split(",");
    //     myArray.forEach(i => {
    //       cy.get('mat-option > .mat-option-text').contains(i).click({ force: true });
    //     });
    //   } else if (testData.role == 'zila_accountant') {
    //     let myArray = testData.zila_to_be_selected.split(",");
    //     myArray.forEach(i => {
    //       cy.get('mat-option > .mat-option-text').contains(i).click({ force: true });
    //     });
    //   } else if (testData.role == 'mandal_accountant') {
    //     let myArray = testData.mandal_to_be_selected.split(",");
    //     cy.wait(1000);
    //     myArray.forEach(i => {
    //       cy.log(i);
    //       cy.wait(2000);
    //       cy.get('mat-option > .mat-option-text').contains(i).click({force: true});
    //     });
    //     //cy.get('#mat-option-917 > .mat-option-text').click({force: true});

    //   }
    // }

    if (testData.Allow_data_download != 0) {

      cy.get('.mat-checkbox').contains('Allow Data Download').click({ force: true });
    }


    if (testData.date_of_transaction != 0) {
      if (testData.fifteen_days != 0) {

        cy.get('.mat-checkbox').contains('15 Days').click({ force: true });
      }

      if (testData.thirty_days != 0) {

        cy.get('.mat-checkbox').contains('30 Days').click({ force: true });
      }
    }

    if (testData.Indian_Donation_Form != 0) {
      if (testData.create != 0) {

        cy.get('.mat-checkbox').contains('Create').click({ force: true });
      }

      if (testData.Edit_within_fifteen_days != 0) {

        cy.get('.mat-checkbox').contains('Edit within 15 Days').click({ force: true });
      }

      if (testData.Edit_within_fifty_days != 0) {

        cy.get('.mat-checkbox').contains('Edit within 30 Days').click({ force: true });
      }

      if (testData.Edit_Lifetime != 0) {

        cy.get('.mat-checkbox').contains('Edit Lifetime').click({ force: true });
      }

      if (testData.Supplementary_Entry != 0) {

        cy.get('.mat-checkbox').contains('Supplementary Entry').click({ force: true });
      }

      if (testData.Allow_Receipt_Print != 0) {
        cy.get('.mat-checkbox').contains('Allow Receipt Print').click({ force: true });
      }

    }

    if (testData.Party_Unit != 0) {
      if (testData.role != 'zila_accountant' && testData.role != 'mandal_accountant') {
        if (testData.State != 0) {
          //cy.get('#mat-checkbox-10 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force: true});
          //cy.get('input[type="checkbox"]').uncheck('131',{force:true});
          //cy.get('input[type="checkbox"]').contains('State').click({force:true});
          //cy.get('#mat-checkbox-10 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
          cy.get('.mat-checkbox').contains('State').click({ force: true });
        }
      }

      if (testData.role != 'mandal_accountant') {
        if (testData.Zila != 0) {
          //cy.get('#mat-checkbox-11 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force: true});
          //cy.get('input[type="checkbox"]').check('132', {force: true});
          cy.get('.mat-checkbox').contains('Zila').click({ force: true });
        }
      }

      if (testData.Mandal != 0) {
        //cy.get('#mat-checkbox-12 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force: true});
        //cy.get('input[type="checkbox"]').check('133',{force:true});
        cy.get('.mat-checkbox').contains('Mandal').click({ force: true });
      }

    }


    //Submit button
    cy.get('button').contains('Submit').click({force:true});

  });
});
