/// <reference types="cypress" />

describe("user-management", () => {

    let testData;
    beforeEach(function () {
        cy.fixture('user-management-data/user-management-visibility').then(function (data) {
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

       // cy.get('.ng-star-inserted > .action-text').click();

        if(testData.National_Accountant !=0)
        {
          // User Management
            cy.get('.action-text').contains('User Management').should('not.exist');
        }

        else if(testData.National_Treasurer !=0)
        {    // User Management
              cy.get('.action-text').contains('User Management').should('exist');
        }

        else if(testData.State_Treasurer !=0)
        {   // User Management
          cy.get('.action-text').contains('User Management').should('exist');
        }

        else if(testData.State_Accountant !=0)
        {   // User Management
          cy.get('.action-text').contains('User Management').should('not.exist');
        }

        else if(testData.Zila_Accountant!=0)
        {   // User Management
          cy.get('.action-text').contains('User Management').should('not.exist');
        }

        else if(testData.Mandal_Accountant!=0)
        {   // User Management
          cy.get('.action-text').contains('User Management').should('not.exist');
        }
    });
})
