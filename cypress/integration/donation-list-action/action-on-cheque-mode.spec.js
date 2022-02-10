/// <reference types="cypress" />

describe("Action On Cheque Mode Payment", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('donation-list-action-data/cheque-action-data').then(function (data) {
      testData = data;
      return testData;
    });
  })

  it('login,actions related to cheque mode payment', () => {
    cy.visit("/");
    cy.get("input[type=email").type(testData.email);
    cy.get("input[type=password").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);
    //click on Total Forms Entered
    cy.get('.count').click();

    // click on cheque button
    cy.get('.mat-tab-label').contains('Cheque').click({ force: true });
    cy.wait(2000);
    let row = testData.select_row + 1;

    function row_and_next_page() {
      // no of time click for next page
      for (let n = 0; n < testData.click_no_of_times_next_page; n++) {
        cy.get('.mat-paginator-navigation-next > .mat-button-wrapper > .mat-paginator-icon')
          .click({ force: true })
      }
      cy.wait(1000);
      // select row
      cy.get(':nth-child(' + row + ') > .action-btn > .mat-focus-indicator > .mat-button-wrapper > .mat-icon').click({ force: true });
      cy.get('.mat-menu-content').should('be.visible');
    }

    row_and_next_page();

    if (testData.flag_operation == 'Edit') {
      cy.wait(1000);
      cy.get('.mat-menu-content').contains('Edit').click({ multiple: true });
      if (testData.flag_cheque_or_DD_front != 0) {
        const imageFile1 = 'images/one.jpg';
        const imageFile2 = 'images/two.png';
        cy.wait(1000);
        cy.get('input[type=file]').attachFile(imageFile1).attachFile(imageFile2);
      }

      if (testData.flag_cheque_or_DD_back != 0) {
        const imageFile1 = 'images/one.jpg';
        const imageFile2 = 'images/two.png';
        cy.wait(1000);
        cy.get('input[type=file]').eq(1).attachFile(imageFile).attachFile(imageFile2);
      }

      if (testData.flag_account_no != 0) {
        cy.get('[formcontrolname="account_number"]').clear();

        cy.get('[formcontrolname="account_number"]').type(testData.account_no_value);

      }

      if (testData.flag_ifsc_code != 0) {
        cy.get('[formcontrolname="ifsc_code"]').clear();
        cy.get('[formcontrolname="ifsc_code"]').type(testData.ifsc_code_value);

        cy.wait(2000);
        cy.get('.bank-details').click({ force: true });
      }

      let surname1st_letter = "";
      let name_value = "";
      if (testData.flag_name != 0) {
        cy.wait(2000);
        cy.get('[formcontrolname="name"]').clear();

        name_value = testData.name_value.trim();

        console.log("donor_name.length :" + name_value.length);
        if (name_value.length > 150) {
          cy.wrap(1).should('eq', 2, { message: 'Name is of more than 150 character' });
        }
        // index of space in donor name
        let space_in_name_value = name_value.indexOf(" ");
        surname1st_letter = name_value.charAt(space_in_name_value + 1).toUpperCase();

        cy.get('[formcontrolname="name"]').type(name_value);
      }
      var name1 = "";
      cy.get('[formcontrolname="name"]').invoke('val').then((myValue) => {
        name1 = myValue;
      });

      cy.wait(1000).then(() => {

        if (testData.flag_mobile != 0) {
          cy.get('[formcontrolname="phone"]').clear();
          cy.get('[formcontrolname="phone"]').type(testData.donor_mobile_value);
        }

        if (testData.flag_email != 0) {
          cy.get('[formcontrolname="email"]').clear();
          cy.get('[formcontrolname="email"]').type(testData.donor_email_value);
        }


        if (testData.flag_house_no != 0) {
          cy.get('[formcontrolname="house"]').clear();
          cy.get('[formcontrolname="house"]').type(testData.house_no_value);
        }

        if (testData.flag_locality_landmark != 0) {
          cy.get('[formcontrolname="locality"]').clear();
          cy.get('[formcontrolname="locality"]').type(testData.locality_landmark_value);
        }

        if (testData.flag_pin_code != 0) {
          cy.get('[formcontrolname="pincode"]').clear();
          cy.get('[formcontrolname="pincode"]').type(testData.pin_code_value);
        }

        let flag_4th_pan_letter = "";
        let flag_5th_pan_letter = "";

        if (testData.flag_category != 0) {
          cy.wait(2000);
          cy.get('input[type="radio"]').check(testData.flag_category_value, { force: true });
          flag_5th_pan_letter = name1.charAt(0).toUpperCase();

          cy.get('input[type="radio"]').check(testData.flag_category_value, { force: true });
          if (testData.flag_category_value == 'individual') {

            flag_4th_pan_letter = "P";
            flag_5th_pan_letter = surname1st_letter;

            if (testData.flag_proprietorship != 0) {
              cy.wait(3000);
              if (testData.flag_proprietorship_changeTo == 'yes') {
                cy.get('input[type="radio"]').check('true', { force: true });

                cy.get('[formcontrolname="proprietorship_name"]').clear();

                cy.get('[formcontrolname="proprietorship_name"]')
                  .type(testData.flag_proprietorship_name);

                let proprietorship_name = testData.flag_proprietorship_name;

                // index of space in proprietorship_name for getting surname of proprietorship name
                let space_in_proprietorship_name = proprietorship_name.indexOf(" ");

                let proprietorship_surname1st_letter = proprietorship_name.charAt(space_in_proprietorship_name + 1).toUpperCase();
                flag_5th_pan_letter = proprietorship_surname1st_letter;
              } else {
                cy.get('input[type="radio"]').check('false', { force: true });
              }
            }
          }
          // check category and set flag 4th character to compare with pan at same place
          else if (testData.flag_category_value == 'huf') {
            //here flag_4th_pan_letter migh
            flag_4th_pan_letter = "H";
          } else if (testData.flag_category_value == 'partnership') {
            flag_4th_pan_letter = "F";
          } else if (testData.flag_category_value == 'trust') {
            flag_4th_pan_letter = "T";
          } else if (testData.flag_category_value == 'corporation') {
            flag_4th_pan_letter = "C";
          } else if (testData.flag_category_value == 'others') {
            cy.get('[formcontrolname="other_category"]')
              .type(testData.other_category_value);

            //here flag_4th_pan_letter might be A/B/J
            flag_4th_pan_letter = "A";
          }
          cy.log("flag_4th_pan_letter ==>" + flag_4th_pan_letter);
          cy.log("flag_5th_pan_letter ==>" + flag_5th_pan_letter);

        }
        if (testData.flag_pan_no != 0) {
          cy.wait(1000);
          cy.get('.otp-input').first().clear().type(testData.pan1);
          cy.get('.otp-input').eq(1).type(testData.pan2);
          cy.get('.otp-input').eq(2).type(testData.pan3);
          cy.get('.otp-input').eq(3).type(testData.pan4);

          if (flag_4th_pan_letter != testData.pan4.toUpperCase()) {
            cy.log('**wrong pan number as 4th character mismatch with category**');
          }
          // check 5th letter of PAN if category is individual
          if (testData.pan5.toUpperCase() != flag_5th_pan_letter) {
            cy.log('**wrong pan number as 5th character mismatch with category**');
          }
          cy.get('.otp-input').eq(4).type(testData.pan5);
          cy.get('.otp-input').eq(5).type(testData.pan6);
          cy.get('.otp-input').eq(6).type(testData.pan7);
          cy.get('.otp-input').eq(7).type(testData.pan8);
          cy.get('.otp-input').eq(8).type(testData.pan9);
          cy.get('.otp-input').eq(9).type(testData.pan10);

          cy.get('body').then((body) => {
            // if following input filed exist means mismatch in pan character and category
            if (body.find('[placeholder="Remarks if any"]').length > 0) {
              cy.get('[placeholder="Remarks if any"]').clear().type(testData.wrong_pan_remark);
            } else {

              cy.log('**pan is perfect**');
            }
          })
        }

        if (testData.flag_amount != 0) {
          // check format of amount and digits after decimal
          if (typeof testData.amount_value == 'number') {
            let amount = testData.amount_value;
            amount = Math.round((amount + Number.EPSILON) * 100) / 100;

            cy.get('[formcontrolname="amount"]').clear();
            cy.get('[formcontrolname="amount"]').type(amount);
          } else {
            cy.wrap(3).should('eq', 4, { message: 'Amount is not in number formate' });
          }

        }

        if (testData.flag_narration != 0) {

          cy.get('[formcontrolname="narration"]').clear();
          cy.get('[formcontrolname="narration"]').type(testData.narration_value);
        }

        if (testData.flag_collector_name != 0) {
          let collector_name = testData.collector_name_value;

          if (collector_name.length > 150) {
            cy.wrap(4).should('eq', 5, { message: 'collector Name is of more than 150 characters' });
          }
          cy.get('[formcontrolname="collector_name"]').clear();
          cy.get('[formcontrolname="collector_name"]').type(collector_name);
        }

        if (testData.flag_collector_mobile != 0) {
          cy.get('[formcontrolname="collector_phone"]').clear();
          cy.get('[formcontrolname="collector_phone"]').type(testData.collector_mobile_value);
        }

        if (testData.flag_nature_of_donation != 0) {
          if (testData.donation_type == 'Voluntary Contribution') {
            cy.log('inside Voluntary Contribution');

            cy.wait(2000);
            cy.get('input[type="radio"]').check(testData.donation_type, { force: true });
          } else if (testData.donation_type == 'Aajivan Sahyog Nidhi') {
            cy.wait(1000);
            cy.get('input[type="radio"]').check('Aajivan Sahyog Nidhi.', { force: true });
          } else {
            cy.wait(1500);
            cy.get('input[type="radio"]').check('Other', { force: true });
            cy.log('inside other');
            cy.get('[formcontrolname="other_nature_of_donation"]').clear();
            cy.get('[formcontrolname="other_nature_of_donation"]').type(testData.other_donation_value);
          }
        }

        if (testData.flag_party_unit != 0) {
          cy.get('input[type="radio"]').check(testData.party_unit_value, { force: true });

          if (testData.party_unit_value == 'Mandal') {
            cy.wait(4000);
            //if state applicable then select
            cy.get('body').then((body) => {
              //if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {
              if (body.find('.d-flex > :nth-child(1) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {

                cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
                  .contains(testData.party_unit_state).click({ force: true });
              } else {

                cy.log('**state is not there**');
              }
            });
            cy.wait(1000);
            //if zila applicable then select
            cy.get('body').then((body) => {
              //if (body.find('[ng-reflect-placeholder="Select zila"]').length > 0) {
              if (body.find('.d-flex > :nth-child(2) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {

                cy.get('.ng-placeholder').contains('Select zila').click().get('ng-select')
                  .contains(testData.party_unit_zila).click({ force: true });
              } else {

                cy.log('**Zila is not there**');
              }
            });

            cy.get('.ng-placeholder').contains('Select mandal').click().get('ng-select')
              .contains(testData.party_unit_mandal).click({ force: true });

          } else if (testData.party_unit_value == 'Zila') {
            cy.wait(2000);
            cy.get('body').then((body) => {
              //if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {
              if (body.find('.d-flex > :nth-child(1) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {

                cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
                  .contains(testData.party_unit_state).click({ force: true });
              } else {

                cy.log('**state is not there**');
              }
            })
            cy.get('.ng-placeholder').contains('Select zila').click().get('ng-select')
              .contains(testData.party_unit_zila).click({ force: true });
          } else {
            cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
              .contains(testData.party_unit_state).click({ force: true });
          }
        }
      });
      // click on update button
      cy.get('button').contains('Update').click();
    } else if (testData.flag_operation == "Archive") {
      cy.get('.mat-menu-content').contains('Archive').click({ multiple: true });
      cy.get('#mat-dialog-0').should('be.visible');
      // choose Yes or No for doing Archive
      //cy.get('.bg-primary > .mat-button-wrapper').click();
      cy.get('.mat-button-wrapper').contains(testData.flag_archive).click({ force: true });
    } else if (testData.flag_operation == "Reversed") {
      cy.get('.mat-menu-content').contains('Reversed').click({ multiple: true });
      cy.get('#mat-dialog-0').should('be.visible');
      cy.get('[formcontrolname="remark"]').type(testData.Reversed_remark);

      // click on submit or Close of reversed
      cy.get('button').contains(testData.flag_reversed).click({ force: true });

    } else if (testData.flag_operation == "Bounced") {
      cy.get('.mat-menu-content').contains('Bounced').click({ multiple: true });
      cy.get('#mat-dialog-0').should('be.visible');
      cy.get('[formcontrolname="remark"]').type(testData.Bounced_remark);

      // click on submit or Close of Bounced
      cy.get('button').contains(testData.flag_bounced).click({ force: true });
    } else if (testData.flag_operation == "Realized") {

      // we can put Realized date by 2 way, 1 by typing and another by selecting from gui
      if (testData.flag_gui != 0) {
        cy.log('GUI selected..');
        cy.get('.mat-header-row > .cdk-column-action').click({ force: true });
        cy.get('.mat-menu-content').contains('Realized').click({ multiple: true });
        cy.log("Through GUI Selection");
        cy.get('div[style=""] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-suffix > .mat-datepicker-toggle > .mat-focus-indicator > .mat-button-wrapper > .mat-datepicker-toggle-default-icon')
          .click({ force: true });
        cy.wait(2000);

        // click no of times calender gui back button
        for (let back = 0; back < testData.no_of_times_calender_back; back++) {
          cy.get('.mat-calendar-previous-button').click({ force: true });
        }
        //select only digit for date
        cy.get('.mat-calendar-table').contains(testData.date_digit).click({ force: true });
      } else {
        cy.log('without gui');

        let today = new Date();
        let today_dd = today.getDate();
        let today_mm = today.getMonth() + 1;
        let today_yyyy = today.getFullYear();


        let today_date1 = new Date(today_yyyy, today_mm, today_dd);
        cy.log('today is the date :' + today_date1);

        var realize = testData.Realized_date;
        var realize_arr = realize.split('/');
        var realize_dd = realize_arr[0];
        var realize_mm = realize_arr[1];
        var realize_yyyy = realize_arr[2];

        let realized_date1 = new Date(realize_yyyy, realize_mm, realize_dd);
        cy.log('realized_date is the date :' + realized_date1);

        // click on edit and copy cheque or dd date and come back to home
        cy.get('.mat-menu-content').contains('Edit').click({ multiple: true });

        cy.wait(2000);

        function check_Realized_Date() {

          let cheque_or_draft = "test value";

          cy.get("body").then($body => {
            if ($body.find('[formcontrolname="date_of_cheque"]').length > 0) {
              // for cheque mode
              cy.get('[formcontrolname="date_of_cheque"]').invoke('val').then((myValue) => {
                cy.log("cheque date:" + myValue);
                cheque_or_draft = myValue;

              })
            }
          });

          cy.wait(3000).then(() => {

            var cheque_arr = cheque_or_draft.split('/');
            var cheque_dd = cheque_arr[0];
            var cheque_mm = cheque_arr[1];
            var cheque_yyyy = cheque_arr[2];

            let cheque_date1 = new Date(cheque_yyyy, cheque_mm, cheque_dd);

            if (cheque_date1.getTime() > realized_date1.getTime()) {
              cy.wrap(1).should('eq', 2, { message: 'realized dates do not fall between cheque and current date' });
            } else if (cheque_date1.getTime() == realized_date1.getTime()) {
              cy.log("realized date and cheque date pass");
            } else if (today_date1.getTime() >= realized_date1.getTime()) {
              cy.log("current date and realized date pass");
            } else {
              cy.wrap(2).should('eq', 3, { message: 'realized dates should not be more than today' });
            }

          })

        }

        check_Realized_Date();

        // click on back icon after copying draft date or cheque date
        cy.get('.back-icon').click();

        // click on cheque button
        cy.get('.mat-tab-label').contains('Cheque').click({ force: true });
        cy.wait(2000);

        row_and_next_page();

        // click on option given from json to perform action
        cy.get('.mat-menu-content').contains(testData.flag_operation).click({ multiple: true });

        cy.wait(1000);

        cy.get('[placeholder="Enter date"]').type(testData.Realized_date);

      }
      //click on Submit or Close for realized
      cy.get('.btn').contains(testData.flag_realized).click({ force: true });

    } else if (testData.flag_operation == "View") {
      cy.get('.mat-menu-content').contains('View').click({ multiple: true });
    }
  });
});
