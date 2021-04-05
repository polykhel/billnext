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

describe('Activity e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.keycloackLogin(oauth2Data, 'user');
    });
    cy.intercept('GET', '/api/activities*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.keycloackLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load Activities', () => {
    cy.intercept('GET', '/api/activities*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Activity').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Activity page', () => {
    cy.intercept('GET', '/api/activities*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('activity');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Activity page', () => {
    cy.intercept('GET', '/api/activities*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Activity');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Activity page', () => {
    cy.intercept('GET', '/api/activities*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Activity');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  /* this test is commented because it contains required relationships
  it('should create an instance of Activity', () => {
    cy.intercept('GET', '/api/activities*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({force: true});
    cy.getEntityCreateUpdateHeading('Activity');

    cy.get(`[data-cy="date"]`).type('2021-03-13T16:32').invoke('val').should('equal', '2021-03-13T16:32');


    cy.get(`[data-cy="amount"]`).type('38418').should('have.value', '38418');


    cy.get(`[data-cy="remarks"]`).type('Baht PCI Markets', { force: true }).invoke('val').should('match', new RegExp('Baht PCI Markets'));


    cy.get(`[data-cy="type"]`).select('INCOME');

    cy.setFieldSelectToLastOfEntity('user');

    cy.setFieldSelectToLastOfEntity('wallet');

    cy.setFieldSelectToLastOfEntity('category');

    cy.get(entityCreateSaveButtonSelector).click({force: true});
    cy.scrollTo('top', {ensureScrollable: false});
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/activities*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });
  */

  /* this test is commented because it contains required relationships
  it('should delete last instance of Activity', () => {
    cy.intercept('GET', '/api/activities*').as('entitiesRequest');
    cy.intercept('DELETE', '/api/activities/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('activity');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({force: true});
        cy.getEntityDeleteDialogHeading('activity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({force: true});
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/activities*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('activity');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
  */
});
