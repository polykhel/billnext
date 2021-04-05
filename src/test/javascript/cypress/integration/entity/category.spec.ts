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

describe('Category e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.keycloackLogin(oauth2Data, 'user');
    });
    cy.intercept('GET', '/api/categories*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.keycloackLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load Categories', () => {
    cy.intercept('GET', '/api/categories*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Category').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Category page', () => {
    cy.intercept('GET', '/api/categories*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('category');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Category page', () => {
    cy.intercept('GET', '/api/categories*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Category');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Category page', () => {
    cy.intercept('GET', '/api/categories*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Category');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  /* this test is commented because it contains required relationships
  it('should create an instance of Category', () => {
    cy.intercept('GET', '/api/categories*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({force: true});
    cy.getEntityCreateUpdateHeading('Category');

    cy.get(`[data-cy="name"]`).type('Table', { force: true }).invoke('val').should('match', new RegExp('Table'));


    cy.get(`[data-cy="type"]`).select('INCOME');

    cy.setFieldSelectToLastOfEntity('user');

    cy.get(entityCreateSaveButtonSelector).click({force: true});
    cy.scrollTo('top', {ensureScrollable: false});
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/categories*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });
  */

  /* this test is commented because it contains required relationships
  it('should delete last instance of Category', () => {
    cy.intercept('GET', '/api/categories*').as('entitiesRequest');
    cy.intercept('DELETE', '/api/categories/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('category');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({force: true});
        cy.getEntityDeleteDialogHeading('category').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({force: true});
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/categories*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('category');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
  */
});
