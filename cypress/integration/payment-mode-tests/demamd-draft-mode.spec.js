
/// <reference types="cypress" />


describe("Demand Draft Payment Mode", () => {
  let testData;
  beforeEach(function () {
    cy.fixture('mode-of-payments-data/dd-mode').then(function (data) {
      testData = data;
      return testData;
    })
  })

  it('login,fill details for demand draft mode payment & submit', () => {
    cy.visit("/");

    //email
    cy.get("input[type=email").type(testData.email);
    //password
    cy.get("input[type=password").type(testData.password).type('{enter}');
    //otp
    cy.get('#otp').type(testData.OTP).type('{enter}');

    // click on indian donation form
    cy.get('.forms-container > .ng-star-inserted').click();
    cy.wait(2000);

    //select demand draft on radio button
    cy.get('input[type="radio"]').check('4', { force: true });

    const imageFile1 = 'images/one.jpg';
    cy.get('input[type=file]').attachFile(imageFile1).attachFile(imageFile1);

    const imageFile2 = 'images/two.png';
    cy.get('input[type=file]').eq(1).attachFile(imageFile1).attachFile(imageFile2);

    function getCurrentFinancialYear() {
      var fiscalyear = "";
      var today = new Date();
      if ((today.getMonth() + 1) <= 3) {
        fiscalyear = (today.getFullYear() - 1) + "-" + today.getFullYear()
      } else {
        fiscalyear = today.getFullYear() + "-" + (today.getFullYear() + 1)
      }
      return fiscalyear
    }

    var last_4_digit_financial_year = getCurrentFinancialYear().substring(5);


    cy.wait(1000);

    // here we are taking yesterday date for new creating transaction

    let today = new Date();
    let yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    let dd1 = yesterday.getDate();
    let mm1 = yesterday.getMonth() + 1;
    let yyyy1 = yesterday.getFullYear();

    let yesterdayDate = (dd1 < 10 ? '0' + dd1 : dd1) + '/' + (mm1 < 10 ? '0' + mm1 : mm1) + '/' + yyyy1;
    cy.log("**yesterdayDate :**" + yesterdayDate);

    var dd_date = yesterdayDate;
    var dd_date_arr = dd_date.split('/');
    var dd_date_dd = dd_date_arr[0];
    var dd_date_mm = dd_date_arr[1];
    var dd_date_yyyy = dd_date_arr[2];

    var priorDate = new Date();
    priorDate.setDate(priorDate.getDate() - 90);

    var dd = priorDate.getDate();
    var mm = priorDate.getMonth() + 1;
    var yyyy = priorDate.getFullYear();

    var finalDate = dd + "/" + mm + "/" + yyyy;
    console.log("90th date :" + finalDate);

    var g2_90_date = new Date(yyyy, mm, dd);

    var g1_draft_date = new Date(dd_date_yyyy, dd_date_mm, dd_date_dd);

    // financial year
    var financial_y = last_4_digit_financial_year;
    console.log("financial_y :" + financial_y);
    var financial_m = "04";
    var financial_d = "01";

    var end_financial_date1 = new Date(financial_y, financial_m, financial_d);

    console.log("end_financial_date1 :" + end_financial_date1);
    console.log("g1_draft_date :" + g1_draft_date);

    if (g1_draft_date.getTime() > end_financial_date1.getTime()) {
      cy.wrap(3).should('eq', 4, { message: 'dd date is > this financial year date' });
    }


    if (g1_draft_date.getTime() >= g2_90_date.getTime()) {
      // transaction date
      //cy.get('#mat-input-17').type(testData.transaction_date);
      cy.get('#mat-input-17').type(dd_date);
    } else if (g1_draft_date.getTime() < g2_90_date.getTime()) {
      cy.wrap(4).should('eq', 5, { message: 'dd date is smaller than 90th date from current date' });
    }

    function test_same_digit(num) {
      var first = num % 10;
      while (num) {
        if (num % 10 !== first) return false;
        num = Math.floor(num / 10);
      }
      return true
    }

    if (test_same_digit(testData.draft_no)) {
      cy.wrap(5).should('eq', 6, { message: 'dd number digits are same' });
    }
    // randomly generate 6 digits cheque number
    let dd_no = Math.floor(100000 + Math.random() * 900000);
    cy.log('**dd no:**' + dd_no);

    cy.get('#mat-input-16').type(dd_no);
    cy.get('#mat-input-18').type(testData.account_no);
    cy.get('#mat-input-19').type(testData.ifcs_code);
    cy.wait(2000);
    cy.get('.bank-details', { timeout: 10000 }).click();

    let donor_name = testData.donor_name.trim();

    console.log("donor_name.length :" + donor_name.length);
    if (donor_name.length > 150) {
      cy.wrap(1).should('eq', 2, { message: 'Name is of more than 150 character' });
    }
    let space_in_donor_name = donor_name.indexOf(" ");
    let surname1st_letter = donor_name.charAt(space_in_donor_name + 1).toUpperCase();


    cy.get('#mat-input-4').type(donor_name);
    cy.get('#mat-input-5').type(testData.mobile_no);
    cy.get('#mat-input-6').type(testData.donor_email);
    cy.get('input[type="radio"]').check(testData.category, { force: true });

    let flag_4th_pan_letter = "";
    let flag_5th_pan_letter = donor_name.charAt(0).toUpperCase();

    cy.wait(1000);
    // if category is individual then set flag 4th & 5th character of donor name
    if (testData.category == 'individual') {
      cy.get('input[type="radio"]').check(testData.proprietorship, { force: true });
      flag_4th_pan_letter = "P";
      flag_5th_pan_letter = surname1st_letter;

      cy.wait(1000);
      // if proprietorship is yes
      if (testData.proprietorship != 'false') {
        cy.get('#mat-input-23').type(testData.proprietorship_name);

        let proprietorship_name = testData.proprietorship_name;
        let space_in_proprietorship_name = proprietorship_name.indexOf(" ");
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
    cy.wait(2000);

    cy.get('.otp-input').first().type(testData.pan1);
    cy.get('.otp-input').eq(1).type(testData.pan2);
    cy.get('.otp-input').eq(2).type(testData.pan3);

    cy.wait(3000);
    // check 4th & 5th character of pan according to donor_name & selected category
    if (testData.category == 'huf') {
      flag_4th_pan_letter = "H";
    } else if (testData.category == "partnership") {
      flag_4th_pan_letter = "F";
    } else if (testData.category == "trust") {
      flag_4th_pan_letter = "T";
    } else if (testData.category == "corporation") {
      flag_4th_pan_letter = "C";
    }
    cy.get('.otp-input').eq(3).type(testData.pan4);
    if (flag_4th_pan_letter != testData.pan4.toUpperCase()) {
      cy.log('**wrong pan number as 4th character mismatch with category**');
      cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
    }

    cy.get('.otp-input').eq(4).type(testData.pan5);
    // check 5th letter of PAN if category is individual
    if (testData.pan5.toUpperCase() != flag_5th_pan_letter) {
      cy.log('**wrong pan number as 5th character mismatch with category**');
      cy.get('[placeholder="Remarks if any"]').clear();
      cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
    }

    console.log("flag_4th_pan_letter ::: " + flag_4th_pan_letter);
    console.log("flag_5th_pan_letter::: " + flag_5th_pan_letter);

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
    if (typeof amount == 'number') {
      amount = Math.round((amount + Number.EPSILON) * 100) / 100;
      console.log("amount :" + amount);
      cy.get('#mat-input-12').type(amount);

    } else {
      cy.wrap(2).should('eq', 3, { message: 'Amount is not in number formate' });
    }
    cy.get('#mat-input-13').type(testData.narration);

    let collector_name = testData.collector_name.trim();

    if (collector_name.length > 150) {
      cy.wrap(6).should('eq', 7, { message: 'Collector Name is of more than 150 character' });
    }
    cy.get('#mat-input-14').type(collector_name);
    cy.get('#mat-input-15').type(testData.collector_mobile);

    cy.wait(2000);
    //select Nature of Donation
    cy.get('input[type="radio"]').check(testData.nature_of_donation, { force: true });

    if (testData.nature_of_donation == 'Other') {
      if (testData.category == 'others') {
        cy.get('[placeholder="Please specify"]').eq(1).type(testData.other_donation);
      } else {
        cy.get('[placeholder="Please specify"]').type(testData.other_donation);
      }
    }

    // select party unit
    cy.get('input[type="radio"]').check(testData.Party_unit, { force: true });
    if (testData.Party_unit == 'Mandal') {
      cy.wait(3000);
      //if state applicable then select
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
        if (body.find('.d-flex > :nth-child(2) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {

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
    cy.get('button').contains('Submit').click({force:true});
  });
})



/*
/// <reference types="cypress" />

describe("Demand Draft Payment Mode", () => {
  let testData;
  beforeEach(function () {
    cy.fixture('mode-of-payments-data/dd-mode').then(function (data) {
      testData = data;
      return testData;
    })
  })

  it('login,fill details for demand draft mode payment & submit', () => {
    cy.visit("/");

    //email
    cy.get("input[type=email").type(testData.email);
    //password
    cy.get("input[type=password").type(testData.password).type('{enter}');
    //otp
    cy.get('#otp').type(testData.OTP).type('{enter}');

    // click on indian donation form
    cy.get('.forms-container > .ng-star-inserted').click();
    cy.wait(2000);

    //select demand draft on radio button
    cy.get('input[type="radio"]').check('4', {force: true});

    const imageFile1 = 'images/one.jpg';
    cy.get('input[type=file]').attachFile(imageFile1).attachFile(imageFile1);

    const imageFile2 = 'images/two.png';
    cy.get('input[type=file]').eq(1).attachFile(imageFile1).attachFile(imageFile2);

    function getCurrentFinancialYear() {
      var fiscalyear = "";
      var today = new Date();
      if ((today.getMonth() + 1) <= 3) {
        fiscalyear = (today.getFullYear() - 1) + "-" + today.getFullYear()
      } else {
        fiscalyear = today.getFullYear() + "-" + (today.getFullYear() + 1)
      }
      return fiscalyear
    }

    var last_4_digit_financial_year = getCurrentFinancialYear().substring(5);
    ;

    cy.wait(1000);
    var cheque = testData.transaction_date;
    var cheque_arr = cheque.split('/');
    var cheque_dd = cheque_arr[0];
    var cheque_mm = cheque_arr[1];
    var cheque_yyyy = cheque_arr[2];

    var priorDate = new Date();
    priorDate.setDate(priorDate.getDate() - 90);

    var dd = priorDate.getDate();
    var mm = priorDate.getMonth() + 1;
    var yyyy = priorDate.getFullYear();

    var finalDate = dd + "/" + mm + "/" + yyyy;
    console.log("90th date :" + finalDate);

    var g2_90_date = new Date(yyyy, mm, dd);

    var g1_draft_date = new Date(cheque_yyyy, cheque_mm, cheque_dd);

    // financial year
    var financial_y = last_4_digit_financial_year;
    console.log("financial_y :" + financial_y);
    var financial_m = "04";
    var financial_d = "01";

    var end_financial_date1 = new Date(financial_y, financial_m, financial_d);

    console.log("end_financial_date1 :" + end_financial_date1);
    console.log("g1_draft_date :" + g1_draft_date);

    if (g1_draft_date.getTime() > end_financial_date1.getTime()) {
      cy.wrap(3).should('eq', 4, {message: 'dd date is > this financial year date'});
    }


    if (g1_draft_date.getTime() >= g2_90_date.getTime()) {
      cy.get('#mat-input-17').type(testData.transaction_date);
    } else if (g1_draft_date.getTime() < g2_90_date.getTime()) {
      cy.wrap(4).should('eq', 5, {message: 'dd date is smaller than 90th date from current date'});
    }

    function test_same_digit(num) {
      var first = num % 10;
      while (num) {
        if (num % 10 !== first) return false;
        num = Math.floor(num / 10);
      }
      return true
    }

    if (test_same_digit(testData.draft_no)) {
      cy.wrap(5).should('eq', 6, {message: 'dd number digits are same'});
    }

    function dd_digits_count(n) {
      var count = 0;
      if (n >= 1) ++count;
      while (n / 10 >= 1) {
        n /= 10;
        ++count;
      }
      return count;
    }

    //validate draft_no
    if (dd_digits_count(testData.draft_no) == 6) {
      cy.get('#mat-input-16').type(testData.draft_no);
    } else {
      cy.wrap(1).should('eq', 2, {message: 'draft_no is not of 6 digits or not a number'});
    }

    cy.get('#mat-input-18').type(testData.account_no);
    cy.get('#mat-input-19').type(testData.ifcs_code);
    cy.wait(2000);
    cy.get('.bank-details', {timeout: 10000}).click();

    let donor_name = testData.donor_name.trim();

    console.log("donor_name.length :" + donor_name.length);
    if (donor_name.length > 150) {
      cy.wrap(1).should('eq', 2, {message: 'Name is of more than 150 character'});
    }
    let space_in_donor_name = donor_name.indexOf(" ");
    let surname1st_letter = donor_name.charAt(space_in_donor_name + 1).toUpperCase();


    cy.get('#mat-input-4').type(donor_name);
    cy.get('#mat-input-5').type(testData.mobile_no);
    cy.get('#mat-input-6').type(testData.donor_email);
    cy.get('input[type="radio"]').check(testData.category, {force: true});

    let flag_4th_pan_letter = "";
    let flag_5th_pan_letter = donor_name.charAt(0).toUpperCase();

    cy.wait(1000);
    // if category is individual then set flag 4th & 5th character of donor name
    if (testData.category == 'individual') {
      cy.get('input[type="radio"]').check(testData.proprietorship, {force: true});
      flag_4th_pan_letter = "P";
      flag_5th_pan_letter = surname1st_letter;

      cy.wait(1000);
      // if proprietorship is yes
      if (testData.proprietorship != 'false') {
        cy.get('#mat-input-23').type(testData.proprietorship_name);

        let proprietorship_name = testData.proprietorship_name;
        let space_in_proprietorship_name = proprietorship_name.indexOf(" ");
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
    cy.wait(2000);

    cy.get('.otp-input').first().type(testData.pan1);
    cy.get('.otp-input').eq(1).type(testData.pan2);
    cy.get('.otp-input').eq(2).type(testData.pan3);

    cy.wait(3000);
    // check 4th & 5th character of pan according to donor_name & selected category
    if (testData.category == 'huf') {
      flag_4th_pan_letter = "H";
    } else if (testData.category == "partnership") {
      flag_4th_pan_letter = "F";
    } else if (testData.category == "trust") {
      flag_4th_pan_letter = "T";
    } else if (testData.category == "corporation") {
      flag_4th_pan_letter = "C";
    }
    cy.get('.otp-input').eq(3).type(testData.pan4);
    if (flag_4th_pan_letter != testData.pan4.toUpperCase()) {
      cy.log('**wrong pan number as 4th character mismatch with category**');
      cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
    }

    cy.get('.otp-input').eq(4).type(testData.pan5);
    // check 5th letter of PAN if category is individual
    if (testData.pan5.toUpperCase() != flag_5th_pan_letter) {
      cy.log('**wrong pan number as 5th character mismatch with category**');
      cy.get('[placeholder="Remarks if any"]').clear();
      cy.get('[placeholder="Remarks if any"]').type(testData.wrong_pan_remark);
    }

    console.log("flag_4th_pan_letter ::: " + flag_4th_pan_letter);
    console.log("flag_5th_pan_letter::: " + flag_5th_pan_letter);

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
    if (typeof amount == 'number') {
      amount = Math.round((amount + Number.EPSILON) * 100) / 100;
      console.log("amount :" + amount);
      cy.get('#mat-input-12').type(amount);

    } else {
      cy.wrap(2).should('eq', 3, {message: 'Amount is not in number formate'});
    }
    cy.get('#mat-input-13').type(testData.narration);

    let collector_name = testData.collector_name.trim();

    if (collector_name.length > 150) {
      cy.wrap(6).should('eq', 7, {message: 'Collector Name is of more than 150 character'});
    }
    cy.get('#mat-input-14').type(collector_name);
    cy.get('#mat-input-15').type(testData.collector_mobile);

    cy.wait(2000);
    //select Nature of Donation
    cy.get('input[type="radio"]').check(testData.nature_of_donation, {force: true});

    if (testData.nature_of_donation == 'Other') {
      if (testData.category == 'others') {
        cy.get('[placeholder="Please specify"]').eq(1).type(testData.other_donation);
      } else {
        cy.get('[placeholder="Please specify"]').type(testData.other_donation);
      }
    }

    // select party unit
    cy.get('input[type="radio"]').check(testData.Party_unit, {force: true});
    if (testData.Party_unit == 'Mandal') {
      cy.wait(3000);
      //if state applicable then select
      cy.get('body').then((body) => {
        //if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {
          
        if (body.find('.d-flex > :nth-child(1) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {
          cy.get('.ng-placeholder').contains('Select state').click().get('ng-select')
            .contains(testData.state_name).click({force: true});
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
        //if (body.find('[ng-reflect-placeholder="Select state"]').length > 0) {
          if (body.find('.d-flex > :nth-child(2) > .bg-white > .ng-select-container > .ng-value-container > .ng-placeholder').length > 0) {
            
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
    //cy.get('button').contains('Submit').click({force:true});
  });
})

*/