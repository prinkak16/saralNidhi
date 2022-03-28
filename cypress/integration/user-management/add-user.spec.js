/// <reference types="cypress" />

describe("user-management", () => {

    let testData;
    beforeEach(function () {
        cy.fixture('user-management-data/add-user-data').then(function (data) {
            testData = data;
            return testData;
        });
    })

    it('login then click on user-management ', () => {
        cy.visit("/");

        cy.get("input[type=email]").type(testData.Login_email);
        cy.get("input[type=password]").type(testData.Login_password).type('{enter}');

        cy.get('#otp').type(testData.Login_OTP).type('{enter}');
        cy.wait(2000);

        cy.get('.action-text').contains('User Management').should('exist').click();

        cy.get('button').contains('Add User').click();

        cy.get('[formcontrolname="name"]').type(testData.full_name);
        cy.get('[formcontrolname="phone_no"]').type(testData.mobile);
        cy.get('[formcontrolname="email"]').type(testData.email);
        cy.get('[formcontrolname="password"]').type(testData.password);

        cy.get('input[type="radio"]').check(testData.role,{force:true});
        cy.wait(1000);
        cy.get('[formcontrolname="location_ids"]').click({force: true});

        cy.wait(1000);
        //cy.get('mat-option > .mat-option-text').contains('Andhra Pradesh').click({force: true});
        //cy.get('mat-option > .mat-option-text').contains('Haryana').click({force: true});

        if(testData.role=='national_accountant' || testData.role=='state_treasurer' || testData.role=='state_accountant')
        {
            let myArray = testData.state_to_be_selected.split(",");
            myArray.forEach(i => {
                cy.get('mat-option > .mat-option-text').contains(i).click({force: true});
            });
        }
        else if(testData.role=='zila_accountant')
        {
            let myArray = testData.zila_to_be_selected.split(",");
            myArray.forEach(i => {
                cy.get('mat-option > .mat-option-text').contains(i).click({force: true});
            });
        }
        else if(testData.role=='mandal_accountant')
        {
            let myArray = testData.mandal_to_be_selected.split(",");
            myArray.forEach(i => {
                cy.get('mat-option > .mat-option-text').contains(i).click({force: true});
            });
        }

        if(testData.Allow_data_download!=0)
        {
            //cy.get('#mat-checkbox-1 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});

            cy.get('input[type="checkbox"]').check('173',{force:true});
        }

        if(testData.date_of_transaction!=0)
        {
            if(testData.fifteen_days!=0)
            {
                //cy.get('#mat-checkbox-2 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('138',{force:true});
            }

            if(testData.thirty_days!=0)
            {
                //cy.get('#mat-checkbox-3 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('139',{force:true});
            }
        }



        if(testData.Indian_Donation_Form!=0)
        {
            if(testData.create!=0)
            {
                //cy.get('#mat-checkbox-4 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('126',{force:true});
            }

            if(testData.Edit_within_fifteen_days!=0)
            {
                //cy.get('#mat-checkbox-5 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('140',{force:true});
            }

            if(testData.Edit_within_fifty_days!=0)
            {
                //cy.get('#mat-checkbox-6 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('141',{force:true});
            }

            if(testData.Edit_Lifetime!=0)
            {
                //cy.get('#mat-checkbox-7 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('142',{force:true});
            }

            if(testData.Supplementary_Entry!=0)
            {
                //cy.get('#mat-checkbox-8 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('143',{force:true});
            }

            if(testData.Allow_Receipt_Print!=0)
            {
                //cy.get('#mat-checkbox-9 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force:true});
                cy.get('input[type="checkbox"]').check('144',{force:true});
            }

        }

        if(testData.role=="state_treasurer")
        {

            if (testData.nri_donation_form != 0) {
                // This is Edit indide NRI DONATION FORM
                //cy.get('#mat-checkbox-10 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force: true});
                cy.get('input[type="checkbox"]').check('125',{force:true});
            }

        }

        if (testData.Party_Unit != 0)
        {
                if(testData.role !='zila_accountant' && testData.role !='mandal_accountant')
                {
                    if (testData.State != 0)
                    {
                        //cy.get('#mat-checkbox-10 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force: true});
                        cy.get('input[type="checkbox"]').check('131',{force:true});
                    }
                }

               if(testData.role !='mandal_accountant')
               {
                if (testData.Zila != 0) {
                    //cy.get('#mat-checkbox-11 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force: true});
                    cy.get('input[type="checkbox"]').check('132', {force: true});
                }
              }

                if (testData.Mandal != 0) {
                    //cy.get('#mat-checkbox-12 > .mat-checkbox-layout > .mat-checkbox-inner-container').click({force: true});
                    cy.get('input[type="checkbox"]').check('133',{force:true});
                }

        }

        //Submit button
       cy.get('button').contains('Submit').click({force:true});
    });
})
