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
      cy.keycloackLogin(oauth2Data, 'user');
    });
    cy.intercept('GET', '/api/wallets*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('wallet');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.keycloackLogout(oauth2Data);
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
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({force: true});
    cy.getEntityCreateUpdateHeading('Wallet');

    cy.get(`[data-cy="walletGroup"]`).select('OVERDRAFTS');


    cy.get(`[data-cy="name"]`).type('Incredible ivory', { force: true }).invoke('val').should('match', new RegExp('Incredible ivory'));


    cy.get(`[data-cy="amount"]`).type('52064').should('have.value', '52064');


    cy.get(`[data-cy="currency"]`).type('Africa Orchestrator', { force: true }).invoke('val').should('match', new RegExp('Africa Orchestrator'));


    cy.get(`[data-cy="remarks"]`).type('Bedfordshire Steel', { force: true }).invoke('val').should('match', new RegExp('Bedfordshire Steel'));

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
