/// <reference types="cypress" />

describe("Action On RTGS Payment Mode", () => {

    let testData;
    beforeEach(function () {
        cy.fixture('donation-list-action-data/rtgs-action-data').then(function (data) {
            testData = data;
            return testData;
        });
    })

    it('login,actions related to rtgs payment mode', () => {
        cy.visit("/");
        cy.get("input[type=email").type(testData.email);
        cy.get("input[type=password").type(testData.password).type('{enter}');

        cy.get('#otp').type(testData.OTP).type('{enter}');
        cy.wait(1000);


        //click on indian donation form
        cy.get('.forms-container > .ng-star-inserted').click();
        cy.wait(2000);
        //Click on rtgs from radio button
        cy.get('input[type="radio"]').check('2', { force: true });

        // here we are taking yesterday date for new creating transaction
        let today = new Date();
        let yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);

        let dd = yesterday.getDate();
        let mm = yesterday.getMonth() + 1;
        let yyyy = yesterday.getFullYear();

        let yesterdayDate = (dd < 10 ? '0' + dd : dd) + '/' + (mm < 10 ? '0' + mm : mm) + '/' + yyyy;
        cy.log("**yesterdayDate:**" + yesterdayDate);

        //Date of Transaction
        cy.get('#mat-input-17').type(yesterdayDate);

        let random_utr_no = Math.floor(100000 + Math.random() * 900000);
        let UTR_No = 'rtgs' + random_utr_no;


        function test_same_char(input) {
            return input.split('').every(char => char === input[0]);
        }
        if (UTR_No.length <= 22) {
            if (test_same_char(UTR_No)) {
                cy.wrap(5).should('eq', 6, { message: 'UTR number characters are same' });
            }
            else {
                //UTR No
                cy.get('#mat-input-16').type(UTR_No);
            }
        }
        else {
            cy.wrap(1).should('eq', 2, { message: 'UTR_NO should not be more than 22 character' });
        }

        cy.get('#mat-input-18').type(testData.account_no);
        cy.get('#mat-input-19').type(testData.ifcs_code);
        cy.wait(2000);
        cy.get('.bank-details', { timeout: 10000 }).click();

        let donor_name = testData.donor_name.trim();

        console.log("donor_name.length :" + donor_name.length);
        if (donor_name.length > 150) {
            cy.wrap(1).should('eq', 2, { message: 'Name is of more than 150 character' });
        }
        // get index of space in donor name (before surname)
        let space_in_donor_name = donor_name.indexOf(" ");

        //get 1st character of surname of donor
        let surname1st_letter = donor_name.charAt(space_in_donor_name + 1).toUpperCase();

        cy.get('#mat-input-4').type(donor_name);
        cy.get('#mat-input-5').type(testData.mobile_no);
        cy.get('#mat-input-6').type(testData.donor_email);
        cy.get('input[type="radio"]').check(testData.category, { force: true });

        let flag_4th_pan_letter = "";
        let flag_5th_pan_letter = donor_name.charAt(0).toUpperCase();

        cy.wait(1000);
        if (testData.category == 'individual') {
            cy.get('input[type="radio"]').check(testData.proprietorship, { force: true });
            flag_4th_pan_letter = "P";
            flag_5th_pan_letter = surname1st_letter;

            cy.wait(1000);
            // if proprietorship is yes
            if (testData.proprietorship != 'false') {
                cy.get('#mat-input-17').type(testData.proprietorship_name);

                let proprietorship_name = testData.proprietorship_name;
                let space_in_proprietorship_name = proprietorship_name.indexOf(" ");

                // 1st character of surname of proprietorship
                let proprietorship_surname1st_letter = proprietorship_name.charAt(space_in_proprietorship_name + 1).toUpperCase();
                flag_5th_pan_letter = proprietorship_surname1st_letter;
            }
        }
        if (testData.category == 'others') {
            cy.get('[formcontrolname="other_category"]').type(testData.other_category);
            //here flag_4th_pan_letter might be A/B/J
            flag_4th_pan_letter = "A";
        }

        cy.get('#mat-input-7').type(testData.house_no);
        cy.get('#mat-input-8').type(testData.locality);
        cy.get('#mat-input-9').type(testData.pin_code);
        cy.wait(1000);

        cy.get('.otp-input').first().type(testData.pan_1);
        cy.get('.otp-input').eq(1).type(testData.pan_2);
        cy.get('.otp-input').eq(2).type(testData.pan_3);

        cy.wait(2000);

        // check 4th & 5th character of pan according to donor_name & selected category
        if (testData.category == 'huf') {
            flag_4th_pan_letter = "H";
        }
        else if (testData.category == "partnership") {
            flag_4th_pan_letter = "F";
        }
        else if (testData.category == "trust") {
            flag_4th_pan_letter = "T";
        }
        else if (testData.category == "corporation") {
            flag_4th_pan_letter = "C";
        }
        cy.get('.otp-input').eq(3).type(testData.pan_4);
        if (flag_4th_pan_letter != testData.pan_4.toUpperCase()) {
            cy.log('**wrong pan number as 4th character mismatch with category**');
            cy.get('[placeholder="Remarks if any"]').type(testData.remark_for_wrong_pan);
        }

        cy.get('.otp-input').eq(4).type(testData.pan_5);
        // check 5th letter of PAN if category is individual
        if (testData.pan_5.toUpperCase() != flag_5th_pan_letter) {
            cy.log('**wrong pan number as 5th character mismatch with category**');
            cy.get('[placeholder="Remarks if any"]').clear();
            cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
        }

        cy.get('.otp-input').eq(5).type(testData.pan_6);
        cy.get('.otp-input').eq(6).type(testData.pan_7);
        cy.get('.otp-input').eq(7).type(testData.pan_8);
        cy.get('.otp-input').eq(8).type(testData.pan_9);
        cy.get('.otp-input').eq(9).type(testData.pan_10);

        cy.get('body').then((body) => {
            if (body.find('[placeholder="Remarks if any"]').length > 0) {

                cy.get('[placeholder="Remarks if any"]').clear();
                cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
            } else {

                cy.log('**pan is perfect**');
            }
        })

        //check format & convert amount to only 2 digit after decimal if it is more
        let amount = parseFloat(testData.amount);

        if (typeof amount == 'number') {
            amount = Math.round((amount + Number.EPSILON) * 100) / 100;
            console.log("amount :" + amount);
            cy.get('#mat-input-12').type(amount);

        }
        else {
            cy.wrap(2).should('eq', 3, { message: 'Amount is not in number formate' });
        }
        cy.get('#mat-input-13').type(testData.narration);

        let collector_name = testData.collector_name.trim();

        if (collector_name.length > 150) {
            cy.wrap(6).should('eq', 7, { message: 'Collector Name is of more than 150 character' });
        }

        cy.get('#mat-input-14').type(collector_name);
        cy.get('#mat-input-15').type(testData.collector_mobile);

        cy.wait(1000);
        //select Nature of Donation
        cy.get('input[type="radio"]').check(testData.nature_of_donation, { force: true });

        if (testData.nature_of_donation == 'Other') {
            if (testData.category == 'others') {
                cy.get('[placeholder="Please specify"]').eq(1).type(testData.other_donation);
            }
            else {
                cy.get('[placeholder="Please specify"]').type(testData.other_donation);
            }
        }

        cy.get('input[type="radio"]').check(testData.Party_unit, { force: true });
        // select party unit
        if (testData.Party_unit == 'Mandal') {
            cy.wait(2000);
            cy.get('body').then((body) => {
                //if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {
                if (body.find('.d-flex > :nth-child(1) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {

                    cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
                        .contains(testData.state_name).click({ force: true });
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
                        .contains(testData.zila_name).click({ force: true });
                } else {

                    cy.log('**Zila is not there**');
                }
            });

            cy.get('.ng-placeholder').contains('Select mandal').click().get('ng-select')
                .contains(testData.mandal_name).click({ force: true });

        } else if (testData.Party_unit == 'Zila') {
            cy.wait(2000);

            cy.get('body').then((body) => {
                //if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {
                if (body.find('.d-flex > :nth-child(1) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {

                    cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
                        .contains(testData.state_name).click({ force: true });
                } else {

                    cy.log('**state is not there**');
                }
            })

            cy.get('.ng-placeholder').contains('Select zila').click().get('ng-select')
                .contains(testData.zila_name).click({ force: true });

            //else part is state unit
        } else {
            cy.wait(2000);
            cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
                .contains(testData.state_name).click({ force: true });
        }
        // submit
        cy.get('button').contains('Submit').click({ force: true });

        // From here above code is for creation of new cash transaction  

        // click on भारतीय जनता पार्टी text for back to home page
        cy.get('.header-title-span').click({ force: true });

        //click on Total Forms Entered
        cy.get('.count').click();

        // click on RTGS button
        cy.get('.mat-tab-label').contains('RTGS').click({ force: true });
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
                cy.get('.bank-details', { timeout: 10000 }).click();
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


                // if proprietorship is yes
                let proprietorship_name = "";
                let space_in_proprietorship_name = "";
                let proprietorship_surname1st_letter = "";

                let flag_4th_pan_letter = "";
                let flag_5th_pan_letter = "";

                if (testData.flag_category != 0) {
                    cy.wait(2000);
                    cy.get('input[type="radio"]').check(testData.flag_category_value, { force: true });
                    flag_5th_pan_letter = name1.charAt(0).toUpperCase();

                    cy.get('input[type="radio"]').check(testData.flag_category_value, { force: true });
                    if (testData.flag_category_value == 'individual') {
                        //cy.get('input[type="radio"]').check(testData.flag_category_value,{force:true});

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

                                let space_in_proprietorship_name = proprietorship_name.indexOf(" ");

                                let proprietorship_surname1st_letter = proprietorship_name.charAt(space_in_proprietorship_name + 1).toUpperCase();

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
                    }
                    else if (testData.donation_type == 'Aajivan Sahyog Nidhi') {
                        cy.log('inside Aajivan Sahyog Nidhi');
                        cy.wait(2000);
                        cy.get('input[type="radio"]').check('Aajivan Sahyog Nidhi.', { force: true });
                    }
                    else {
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
            });
            // click on update button
            cy.get('button').contains('Update').click();
        }
        else if (testData.flag_operation == "Archive") {
            cy.get('#mat-dialog-0').should('be.visible');
            // choose Yes or No for doing Archive
            //cy.get('.bg-primary > .mat-button-wrapper').click();
            cy.get('.mat-button-wrapper').contains(testData.flag_archive).click({ force: true });
        }
        else if (testData.flag_operation == "Reversed") {
            cy.get('#mat-dialog-0').should('be.visible');
            cy.get('[formcontrolname="remark"]').type(testData.Reversed_remark);

            // click on submit or Close of reversed
            cy.get('button').contains(testData.flag_reversed).click({ force: true });
        }

    });
});

/*

/// <reference types="cypress" />

describe("Action On RTGS Payment Mode", () => {

    let testData;
    beforeEach(function () {
        cy.fixture('donation-list-action-data/rtgs-action-data').then(function (data) {
            testData = data;
            return testData;
        });
    })

    it('login,actions related to rtgs payment mode', () => {
        cy.visit("/");
        cy.get("input[type=email").type(testData.email);
        cy.get("input[type=password").type(testData.password).type('{enter}');

        cy.get('#otp').type(testData.OTP).type('{enter}');
        cy.wait(2000);
        //click on Total Forms Entered
        cy.get('.count').click();

        // click on RTGS button
        cy.get('.mat-tab-label').contains('RTGS').click({ force: true });
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
                cy.get('.bank-details', { timeout: 10000 }).click();
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


                // if proprietorship is yes
                let proprietorship_name = "";
                let space_in_proprietorship_name = "";
                let proprietorship_surname1st_letter = "";

                let flag_4th_pan_letter = "";
                let flag_5th_pan_letter = "";

                if (testData.flag_category != 0) {
                    cy.wait(2000);
                    cy.get('input[type="radio"]').check(testData.flag_category_value, { force: true });
                    flag_5th_pan_letter = name1.charAt(0).toUpperCase();

                    cy.get('input[type="radio"]').check(testData.flag_category_value, { force: true });
                    if (testData.flag_category_value == 'individual') {
                        //cy.get('input[type="radio"]').check(testData.flag_category_value,{force:true});

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

                                let space_in_proprietorship_name = proprietorship_name.indexOf(" ");

                                let proprietorship_surname1st_letter = proprietorship_name.charAt(space_in_proprietorship_name + 1).toUpperCase();

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
                    }
                    else if (testData.donation_type == 'Aajivan Sahyog Nidhi') {
                        cy.log('inside Aajivan Sahyog Nidhi');
                        cy.wait(2000);
                        cy.get('input[type="radio"]').check('Aajivan Sahyog Nidhi.', { force: true });
                    }
                    else {
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
            });
            // click on update button
            cy.get('button').contains('Update').click();
        }
        else if (testData.flag_operation == "Archive") {
            cy.get('#mat-dialog-0').should('be.visible');
             // choose Yes or No for doing Archive
            //cy.get('.bg-primary > .mat-button-wrapper').click();
            cy.get('.mat-button-wrapper').contains(testData.flag_archive).click({ force: true });
        }
        else if (testData.flag_operation == "Reversed") {
            cy.get('#mat-dialog-0').should('be.visible');
            cy.get('[formcontrolname="remark"]').type(testData.Reversed_remark);

            // click on submit or Close of reversed
            cy.get('button').contains(testData.flag_reversed).click({ force: true });
        }

    });
});

 */
