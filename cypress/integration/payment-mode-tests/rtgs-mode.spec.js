/// <reference types="cypress" />


describe("RTGS Payment Mode", () => {
    let testData;
    beforeEach(function () {
        cy.fixture('mode-of-payments-data/rtgs-mode').then(function (data) {
            testData=data;
            return testData;
        })
    })

    it('login,fill details for RTGS mode payment & submit', () => {
        cy.visit("/");
        cy.get("input[type=email]").type(testData.email);
        cy.get("input[type=password]").type(testData.password).type('{enter}');

        cy.get('#otp').type(testData.OTP).type('{enter}');

        //click on indian donation form
        cy.get('.forms-container > .ng-star-inserted').click();
        cy.wait(2000);
      //Click on rtgs from radio button
        cy.get('input[type="radio"]').check('2',{force:true});

        //Date of Transaction
        cy.get('#mat-input-17').type(testData.cur_date);

        let UTR_No=testData.UTR_No;

        function test_same_char(input) {
            return input.split('').every(char => char === input[0]);
        }
        if(testData.UTR_No.length <=22)
        {
            if(test_same_char(UTR_No))
            {
                cy.wrap(5).should('eq', 6, { message: 'UTR number characters are same'});
            }
            else {
                //UTR No
                cy.get('#mat-input-16').type(testData.UTR_No);
            }
        }
        else
        {
            cy.wrap(1).should('eq', 2, { message: 'UTR_NO should be not be more than 22 character'});
        }

        cy.get('#mat-input-18').type(testData.account_no);
        cy.get('#mat-input-19').type(testData.ifcs_code);
        cy.wait(2000);
        cy.get('.bank-details',{ timeout: 10000 }).click();

        let donor_name=testData.donor_name.trim();

        console.log("donor_name.length :"+donor_name.length);
        if(donor_name.length > 150)
        {
            cy.wrap(1).should('eq', 2, { message: 'Name is of more than 150 character'});
        }
        // get index of space in donor name (before surname)
        let space_in_donor_name = donor_name.indexOf(" ");

        //get 1st character of surname of donor
        let surname1st_letter = donor_name.charAt(space_in_donor_name+1).toUpperCase();

        cy.get('#mat-input-4').type(donor_name);
        cy.get('#mat-input-5').type(testData.mobile_no);
        cy.get('#mat-input-6').type(testData.donor_email);
        cy.get('input[type="radio"]').check(testData.category,{force:true});

        let flag_4th_pan_letter="";
        let flag_5th_pan_letter = donor_name.charAt(0).toUpperCase();

        cy.wait(1000);
        if(testData.category=='individual') {
            cy.get('input[type="radio"]').check(testData.proprietorship,{force:true});
            flag_4th_pan_letter="P";
            flag_5th_pan_letter=surname1st_letter;

            cy.wait(1000);
          // if proprietorship is yes
            if(testData.proprietorship!='false') {
                cy.get('#mat-input-17').type(testData.proprietorship_name);

                let proprietorship_name=testData.proprietorship_name;
                let space_in_proprietorship_name = proprietorship_name.indexOf(" ");

                // 1st character of surname of proprietorship
                let proprietorship_surname1st_letter = proprietorship_name.charAt(space_in_proprietorship_name+1).toUpperCase();
                flag_5th_pan_letter=proprietorship_surname1st_letter;
            }
        }
        if(testData.category=='others') {
            cy.get('[formcontrolname="other_category"]').type(testData.other_category);
            //here flag_4th_pan_letter might be A/B/J
            flag_4th_pan_letter="A";
        }

        cy.get('#mat-input-7').type(testData.house_no);
        cy.get('#mat-input-8').type(testData.locality);
        cy.get('#mat-input-9').type(testData.pin_code);
        cy.wait(2000);

        cy.get('.otp-input').first().type(testData.pan1);
        cy.get('.otp-input').eq(1).type(testData.pan2);
        cy.get('.otp-input').eq(2).type(testData.pan3);

        cy.wait(3000);

        // check 4th & 5th character of pan according to donor_name & selected category
        if(testData.category=='huf')
        {
            flag_4th_pan_letter="H";
        }
        else if (testData.category=="partnership")
        {
            flag_4th_pan_letter="F";
        }
        else if (testData.category=="trust")
        {
            flag_4th_pan_letter="T";
        }
        else if (testData.category=="corporation")
        {
            flag_4th_pan_letter="C";
        }
        cy.get('.otp-input').eq(3).type(testData.pan4);
        if(flag_4th_pan_letter!=testData.pan4.toUpperCase())
        {
            cy.log('**wrong pan number as 4th character mismatch with category**');
            cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
        }

        cy.get('.otp-input').eq(4).type(testData.pan5);
        // check 5th letter of PAN if category is individual
        if(testData.pan5.toUpperCase()!=flag_5th_pan_letter)
        {
            cy.log('**wrong pan number as 5th character mismatch with category**');
            cy.get('[placeholder="Remarks if any"]').clear();
            cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
        }

        console.log("flag_4th_pan_letter ::: "+flag_4th_pan_letter);
        console.log("flag_5th_pan_letter::: "+flag_5th_pan_letter);

        //cy.get('.otp-input').eq(4).type(testData.pan5);
        cy.get('.otp-input').eq(5).type(testData.pan6);
        cy.get('.otp-input').eq(6).type(testData.pan7);
        cy.get('.otp-input').eq(7).type(testData.pan8);
        cy.get('.otp-input').eq(8).type(testData.pan9);
        cy.get('.otp-input').eq(9).type(testData.pan10);

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

        if(typeof amount == 'number')
        {
            amount=Math.round((amount + Number.EPSILON) * 100) / 100;
            console.log("amount :"+amount);
            cy.get('#mat-input-12').type(amount);

        }
        else {
            cy.wrap(2).should('eq', 3, { message: 'Amount is not in number formate'});
        }
        cy.get('#mat-input-13').type(testData.narration);

        let collector_name=testData.collector_name.trim();

        if(collector_name.length > 150)
        {
            cy.wrap(6).should('eq', 7, { message: 'Collector Name is of more than 150 character'});
        }

        cy.get('#mat-input-14').type(collector_name);
        cy.get('#mat-input-15').type(testData.collector_mobile);

        cy.wait(2000);
      //select Nature of Donation
        cy.get('input[type="radio"]').check(testData.nature_of_donation,{force:true});

        if(testData.nature_of_donation=='Other')
        {
            if(testData.category=='others') {
                cy.get('[placeholder="Please specify"]').eq(1).type(testData.other_donation);
            }
            else {
                cy.get('[placeholder="Please specify"]').type(testData.other_donation);
            }
        }

        cy.get('input[type="radio"]').check(testData.Party_unit,{force:true});
      // select party unit
        if(testData.Party_unit=='Mandal') {
            cy.wait(3000);
          cy.get('body').then((body) => {
            if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {

              cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
                .contains(testData.state_name).click({force: true});
            } else {

              cy.log('**state is not there**');
            }
          });

          cy.wait(1000);

          //if zila applicable then select
          cy.get('body').then((body) => {
            if (body.find('[ng-reflect-placeholder="Select zila"]').length > 0) {

              cy.get('.ng-placeholder').contains('Select zila').click().get('ng-select')
                .contains(testData.zila_name).click({force: true});
            } else {

              cy.log('**Zila is not there**');
            }
          });

          cy.get('.ng-placeholder').contains('Select mandal').click().get('ng-select')
            .contains(testData.mandal_name).click({force: true});

        } else if (testData.Party_unit == 'Zila') {
          cy.wait(2000);

          cy.get('body').then((body) => {
            if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {

              cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
                .contains(testData.state_name).click({force: true});
            } else {

              cy.log('**state is not there**');
            }
          })

          cy.get('.ng-placeholder').contains('Select zila').click().get('ng-select')
            .contains(testData.zila_name).click({force: true});

          //else part is state unit
        } else {
          cy.wait(2000);
          cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
            .contains(testData.state_name).click({force: true});
        }
        //submit
        //cy.get('button').contains('Submit').click();
    });
})


