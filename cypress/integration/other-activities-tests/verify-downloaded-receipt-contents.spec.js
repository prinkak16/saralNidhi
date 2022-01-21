describe("Test on Receipt", () => {
  let testData;


  beforeEach(function () {
    cy.fixture('receipt-downloaded-data/pass-receipt-content').then(function (data) {
      testData = data;
      return testData;
    })
  })

  it('matching content of downloaded receipt', () => {
    cy.visit("/");
    cy.get("input[type=email]").type(testData.email);
    cy.get("input[type=password]").type(testData.password).type('{enter}');

    cy.get('#otp').type(testData.OTP).type('{enter}');

    //cy.task('getPdfContent', testData.receipt_name).then(content => {

    cy.task('countFiles', 'cypress/downloads').then((file) => {
      cy.log('file in download folder::' + file);
      cy.wait(2000);
      let vk = file.toString();
      cy.task('getPdfContent', vk).then(content => {
        let a = content.text;

        let a1 = a.trim();

        expect(a1).to.contains(testData.Date);
        expect(a1).to.contains(testData.No);
        expect(a1).to.contains(testData.Received_with_thanks_from);
        expect(a1).to.contains(testData.Donor_PAN);
        expect(a1).to.contains(testData.Address);
        expect(a1).to.contains(testData.Locality);
        expect(a1).to.contains(testData.District);
        expect(a1).to.contains(testData.State);
        expect(a1).to.contains(testData.Pincode);
        expect(a1).to.contains(testData.Rupees_in_words);
        expect(a1).to.contains(testData.complete_vide_donation_info);
        expect(a1).to.contains(testData.complete_vide_donation_info);

      })
    })
  })
})
