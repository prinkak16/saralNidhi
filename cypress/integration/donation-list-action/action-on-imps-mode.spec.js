/// <reference types="cypress" />

describe("Action On IMPS Payment Mode", () => {

  let testData;
  beforeEach(function () {
    cy.fixture('donation-list-action-data/imps-action-data').then(function (data) {
      testData = data;
      return testData;
    });
  })


  it('login,actions related to imps mode payment ', () => {
    cy.visit("/");
    cy.get("input[type=email").type(testData.email);
    cy.get("input[type=password").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');
    cy.wait(2000);

    //click on Total Forms Entered
    cy.get('.count').click();

    // click on IMPS button
    cy.get('.mat-tab-label').contains('IMPS').click({ force: true });
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
    cy.get('.mat-menu-content').contains(testData.flag_operation).click({ multiple: true });

    if (testData.flag_operation == 'Edit') {
      cy.wait(1000);

      if (testData.flag_account_no != 0) {
        cy.get('[formcontrolname="account_number"]').clear();

        cy.get('[formcontrolname="account_number"]').type(testData.account_no_value);

      }

      if (testData.flag_ifsc_code != 0) {
        cy.get('[formcontrolname="ifsc_code"]').clear();
        cy.get('[formcontrolname="ifsc_code"]').type(testData.ifsc_code_value);

        cy.wait(2000);
        cy.get('.bank-details', { timeout: 5000 }).click();
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
        let space_in_name_value = name_value.indexOf(" ");
        surname1st_letter = name_value.charAt(space_in_name_value + 1).toUpperCase();


        cy.get('[formcontrolname="name"]').type(name_value);
      }

      var name1 = "";
      cy.get('[formcontrolname="name"]').invoke('val').then((myValue) => {
        name1 = myValue;
      });

      cy.wait(1000).then(() => {
        cy.log("name1 outside :" + name1);

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
                cy.log("proprietorship_name ==>" + proprietorship_name);

                let space_in_proprietorship_name = proprietorship_name.indexOf(" ");

                cy.log("space_in_proprietorship_name ==>" + space_in_proprietorship_name);

                let proprietorship_surname1st_letter = proprietorship_name.charAt(space_in_proprietorship_name + 1).toUpperCase();

                cy.log("proprietorship_surname1st_letter ==>" + proprietorship_surname1st_letter);

                flag_5th_pan_letter = proprietorship_surname1st_letter;

              } else {
                cy.get('input[type="radio"]').check('false', { force: true });
              }
            }
          } else if (testData.flag_category_value == 'huf') {
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

          // let flag_wrong_pan=0;
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
            if (body.find('[placeholder="Remarks if any"]').length > 0) {
              cy.get('[placeholder="Remarks if any"]').clear().type(testData.wrong_pan_remark);
            } else {

              cy.log('**pan is perfect**');
            }
          })
        }

        if (testData.flag_amount != 0) {

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
            cy.log('inside Aajivan Sahyog Nidhi');
            cy.wait(2000);
            cy.get('input[type="radio"]').check('Aajivan Sahyog Nidhi.', { force: true });
          } else {
            cy.wait(2000);
            cy.get('input[type="radio"]').check('Other', { force: true });
            cy.log('inside other');
            cy.get('[formcontrolname="other_nature_of_donation"]').clear();
            cy.get('[formcontrolname="other_nature_of_donation"]').type(testData.other_donation_value);
          }
        }

        if (testData.flag_party_unit != 0) {
          cy.get('input[type="radio"]').check(testData.party_unit_value, { force: true });

          if (testData.party_unit_value == 'Mandal') {
            cy.wait(3000);
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
        cy.log("name1 outside2 :::::: :" + name1);
      });
      // click on update button
      cy.get('button').contains('Update').click();
    } else if (testData.flag_operation == "Archive") {
      cy.get('#mat-dialog-0').should('be.visible');
      // choose Yes or No for doing Archive
      //cy.get('.bg-primary > .mat-button-wrapper').click();
      cy.get('.mat-button-wrapper').contains(testData.flag_archive).click({ force: true });
    } else if (testData.flag_operation == "Reversed") {
      cy.get('#mat-dialog-0').should('be.visible');
      cy.get('[formcontrolname="remark"]').type(testData.Reversed_remark);

      // click on submit or Close of reversed
      cy.get('button').contains(testData.flag_reversed).click({ force: true });
    }

  });
});

/*

describe("IMPS Mode Related Action", () => {

    let testData;
    beforeEach(function () {
        cy.fixture('donation-list-action-data/IMPS_actionData').then(function (data) {
            testData = data;
            return testData;
        });
    })


    it('login then click on total count ', () => {
        cy.visit("/");
        cy.get("input[type=email").type(testData.email);
        cy.get("input[type=password").type(testData.password).type('{enter}');

        cy.get('#otp').type(testData.OTP).type('{enter}');
        cy.wait(2000);
        cy.get('.count').click();

        //cy.get('#mat-input-3').type(testData.pan_no);
        //cy.wait(5000);
        //cy.get('.mat-warn').click({multiple:true});

        // click on IMPS button
        cy.get('#mat-tab-label-0-5 > .mat-tab-label-content > .ng-star-inserted > .tab-text')
            .click();


        cy.wait(3000);

        // hard coded..
        cy.get(':nth-child(2) > .action-btn > :nth-child(2) > .mat-focus-indicator > .mat-button-wrapper > .mat-icon')
            .click({force:true});

        //cy.get('.mat-button-wrapper > .mat-icon').click({force:true});

        cy.get('.mat-menu-content').should('be.visible');
        cy.get('.mat-menu-content').contains(testData.flag_operation).click({multiple:true});

        if(testData.flag_operation=='Edit')
        {
            cy.wait(1000);

            if(testData.flag_account_no!=0)
            {
                cy.get('[formcontrolname="account_number"]').clear();

                cy.get('[formcontrolname="account_number"]').type(testData.account_no_value);

            }

            if(testData.flag_ifsc_code!=0) {
                cy.get('[formcontrolname="ifsc_code"]').clear();
                cy.get('[formcontrolname="ifsc_code"]').type(testData.ifsc_code_value);

                cy.wait(2000);
                cy.get('.bank-details',{ timeout: 5000 }).click();
            }

            if(testData.flag_name!=0)
            {
                cy.wait(2000);
                cy.get('[formcontrolname="name"]').clear();
                cy.get('[formcontrolname="name"]').type(testData.name_value);
            }

            if(testData.flag_mobile!=0)
            {
                cy.get('[formcontrolname="phone"]').clear();
                cy.get('[formcontrolname="phone"]').type(testData.donor_mobile_value);
            }

            if(testData.flag_email!=0)
            {
                cy.get('[formcontrolname="email"]').clear();
                cy.get('[formcontrolname="email"]').type(testData.donor_email_value);
            }

            // if(testData.flag_amount!=0)
            // {
            //     cy.get('[formcontrolname="amount"]').clear();
            //     cy.get('[formcontrolname="amount"]').type(testData.amount_value);
            // }

            if(testData.flag_house_no!=0)
            {
                cy.get('[formcontrolname="house"]').clear();
                cy.get('[formcontrolname="house"]').type(testData.house_no_value);
            }

            if(testData.flag_locality_landmark!=0)
            {
                cy.get('[formcontrolname="locality"]').clear();
                cy.get('[formcontrolname="locality"]').type(testData.locality_landmark_value);
            }

            if(testData.flag_pin_code!=0)
            {
                cy.get('[formcontrolname="pincode"]').clear();
                cy.get('[formcontrolname="pincode"]').type(testData.pin_code_value);
            }

            if(testData.flag_category!=0)
            {    cy.wait(2000);
                cy.get('input[type="radio"]').check(testData.flag_category_value,{force:true});
                if(testData.flag_category_value=='individual') {
                    //cy.get('input[type="radio"]').check(testData.flag_category_value,{force:true});

                    if(testData.flag_proprietorship!=0) {
                        cy.wait(5000);
                        if(testData.flag_proprietorship_changeTo=='yes') {
                            cy.get('input[type="radio"]').check('true',{force:true});

                            cy.get('[formcontrolname="proprietorship_name"]')
                                .type(testData.flag_proprietorship_name);
                        }
                        else
                        {
                            cy.get('input[type="radio"]').check('false',{force:true});
                        }
                    }
                }
                if(testData.flag_category_value=='others')
                {
                    cy.get('[formcontrolname="other_category"]')
                        .clear();
                    cy.get('[formcontrolname="other_category"]')
                        .type(testData.other_category_value);
                }


            }

            if(testData.flag_pan_no!=0)
            {
                cy.wait(1000);
                cy.get('.otp-input').eq(0).clear().type(testData.pan1);
                cy.get('.otp-input').eq(1).type(testData.pan2);
                cy.get('.otp-input').eq(2).type(testData.pan3);
                cy.get('.otp-input').eq(3).type(testData.pan4);
                cy.get('.otp-input').eq(4).type(testData.pan5);
                cy.get('.otp-input').eq(5).type(testData.pan6);
                cy.get('.otp-input').eq(6).type(testData.pan7);
                cy.get('.otp-input').eq(7).type(testData.pan8);
                cy.get('.otp-input').eq(8).type(testData.pan9);
                cy.get('.otp-input').eq(9).type(testData.pan10);
            }

            if(testData.flag_amount!=0)
            {
                cy.get('[formcontrolname="amount"]').clear();
                cy.get('[formcontrolname="amount"]').type(testData.amount_value);
            }

            if(testData.flag_narration!=0)
            {

                cy.get('[formcontrolname="narration"]').clear();
                cy.get('[formcontrolname="narration"]').type(testData.narration_value);
            }

            if(testData.flag_collector_name!=0)
            {
                cy.get('[formcontrolname="collector_name"]').clear();
                cy.get('[formcontrolname="collector_name"]').type(testData.collector_name_value);
            }

            if(testData.flag_collector_mobile!=0)
            {
                cy.get('[formcontrolname="collector_phone"]').clear();
                cy.get('[formcontrolname="collector_phone"]').type(testData.collector_mobile_value);
            }

            if(testData.flag_nature_of_donation!=0)
            {
                if(testData.donation_type=='Voluntary Contribution')
                {
                    cy.log('inside Voluntary Contribution');

                    cy.wait(2000);
                    cy.get('input[type="radio"]').check(testData.donation_type,{force:true});
                }
                else if(testData.donation_type=='Aajivan Sahyog Nidhi')
                {
                    cy.log('inside Aajivan Sahyog Nidhi');
                    cy.wait(2000);
                    cy.get('input[type="radio"]').check('Aajivan Sahyog Nidhi.',{force:true});
                }
                else
                {
                    cy.wait(2000);
                    cy.get('input[type="radio"]').check('Other',{force:true});
                    cy.log('inside other');
                    cy.get('[formcontrolname="other_nature_of_donation"]').clear();
                    cy.get('[formcontrolname="other_nature_of_donation"]').type(testData.other_donation_value);
                }
            }

            if(testData.flag_party_unit!=0)
            {
                cy.get('input[type="radio"]').check(testData.party_unit_value,{force:true});

                if(testData.party_unit_value=='Mandal')
                {
                    cy.wait(4000);
                    cy.get('.d-flex > :nth-child(1) > .bg-white > .ng-select-container > .ng-value-container > .ng-input > input')
                        .click().get('ng-select')
                        .contains(testData.party_unit_state).click();

                    cy.get('.d-flex > :nth-child(2) > .bg-white > .ng-select-container > .ng-value-container > .ng-input > input')
                        .click().get('ng-select')
                        .contains(testData.party_unit_zila).click();

                    cy.get(':nth-child(3) > .bg-white > .ng-select-container > .ng-value-container > .ng-input > input')
                        .click().get('ng-select')
                        .contains(testData.party_unit_mandal).click({force:true});
                }
                else if(testData.party_unit_value=='Zila')
                {
                    cy.wait(3000);
                    cy.get('.d-flex > :nth-child(1) > .bg-white > .ng-select-container > .ng-value-container > .ng-input > input').click()
                        .click().get('ng-select').contains(testData.party_unit_state).click();

                    cy.get('.d-flex > :nth-child(2) > .bg-white > .ng-select-container > .ng-value-container > .ng-input > input')
                        .click().get('ng-select')
                        .contains(testData.party_unit_zila).click();

                }
                else
                {
                    cy.wait(2000);
                    cy.get('.col-md-6 > .bg-white > .ng-select-container > .ng-value-container > .ng-input > input').click()
                        .get('ng-select').contains(testData.party_unit_state).click();
                }
            }

            // click on update button
            //cy.get('button').contains('Update').click();
        }
        else if(testData.flag_operation=="Archive")
        {
            cy.get('#mat-dialog-0').should('be.visible');
            cy.get('.bg-primary > .mat-button-wrapper').click();
        }
        else if(testData.flag_operation=="Reversed")
        {
            cy.get('#mat-dialog-0').should('be.visible');
            cy.get('[formcontrolname="remark"]').type(testData.Reversed_remark);

            // click on Submit button
            //cy.get('button').contains('Submit').click();
        }
        else if(testData.flag_operation=="Bounced")
        {
            cy.get('#mat-dialog-0').should('be.visible');
            cy.get('[formcontrolname="remark"]').type(testData.Bounced_remark);
        }
        else if(testData.flag_operation=="Realized")
        {
            cy.wait(2000);
            // cy.get('[fill="currentColor"]').click({ multiple: true, force: true });

            cy.get('div[style=""] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-suffix > .mat-datepicker-toggle > .mat-focus-indicator > .mat-button-wrapper > .mat-datepicker-toggle-default-icon')
                .click({force: true});
            cy.wait(2000);
            cy.get('.mat-calendar-previous-button').click(); // for back button
            cy.get(':nth-child(4) > [data-mat-col="6"] > .mat-calendar-body-cell-content').click({force:true});

        }

    });
});

 */
