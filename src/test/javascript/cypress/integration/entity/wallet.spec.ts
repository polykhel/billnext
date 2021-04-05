import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Wallet e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load Wallets', () => {
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Wallet').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Wallet page', () => {
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('wallet');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Wallet page', () => {
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Wallet');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Wallet page', () => {
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Wallet');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  /* this test is commented because it contains required relationships
  it('should create an instance of Wallet', () => {
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest')
      .then(({ request, response }) => startingEntitiesCount = response.body.length);
    cy.get(entityCreateButtonSelector).click({force: true});
    cy.getEntityCreateUpdateHeading('Wallet');

    cy.get(`[data-cy="walletGroup"]`).select('LOAN');


    cy.get(`[data-cy="name"]`).type('Republic', { force: true }).invoke('val').should('match', new RegExp('Republic'));


    cy.get(`[data-cy="amount"]`).type('82646').should('have.value', '82646');


    cy.get(`[data-cy="currency"]`).type('Orchestrator Latvia Bike', { force: true }).invoke('val').should('match', new RegExp('Orchestrator Latvia Bike'));


    cy.get(`[data-cy="remarks"]`).type('application Norway and', { force: true }).invoke('val').should('match', new RegExp('application Norway and'));

    cy.setFieldSelectToLastOfEntity('user');

    cy.get(entityCreateSaveButtonSelector).click({force: true});
    cy.scrollTo('top', {ensureScrollable: false});
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/wallets*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });
  */

  /* this test is commented because it contains required relationships
  it('should delete last instance of Wallet', () => {
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.intercept('DELETE', '/api/wallets/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({force: true});
        cy.getEntityDeleteDialogHeading('wallet').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({force: true});
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/wallets*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('wallet');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
  */
});
