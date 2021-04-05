package com.polykhel.billnext.web.rest;

import static com.polykhel.billnext.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.polykhel.billnext.IntegrationTest;
import com.polykhel.billnext.domain.User;
import com.polykhel.billnext.domain.Wallet;
import com.polykhel.billnext.domain.enumeration.WalletGroup;
import com.polykhel.billnext.repository.WalletRepository;
import com.polykhel.billnext.service.dto.WalletDTO;
import com.polykhel.billnext.service.mapper.WalletMapper;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WalletResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WalletResourceIT {

    private static final WalletGroup DEFAULT_WALLET_GROUP = WalletGroup.CASH;
    private static final WalletGroup UPDATED_WALLET_GROUP = WalletGroup.ACCOUNTS;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWalletMockMvc;

    private Wallet wallet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wallet createEntity(EntityManager em) {
        Wallet wallet = new Wallet()
            .walletGroup(DEFAULT_WALLET_GROUP)
            .name(DEFAULT_NAME)
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .remarks(DEFAULT_REMARKS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        wallet.setUser(user);
        return wallet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wallet createUpdatedEntity(EntityManager em) {
        Wallet wallet = new Wallet()
            .walletGroup(UPDATED_WALLET_GROUP)
            .name(UPDATED_NAME)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .remarks(UPDATED_REMARKS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        wallet.setUser(user);
        return wallet;
    }

    @BeforeEach
    public void initTest() {
        wallet = createEntity(em);
    }

    @Test
    @Transactional
    void createWallet() throws Exception {
        int databaseSizeBeforeCreate = walletRepository.findAll().size();
        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);
        restWalletMockMvc
            .perform(
                post("/api/wallets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate + 1);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getWalletGroup()).isEqualTo(DEFAULT_WALLET_GROUP);
        assertThat(testWallet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(DEFAULT_REMARKS);
    }

    @Test
    @Transactional
    void createWalletWithExistingId() throws Exception {
        // Create the Wallet with an existing ID
        wallet.setId(1L);
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        int databaseSizeBeforeCreate = walletRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletMockMvc
            .perform(
                post("/api/wallets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWalletGroupIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setWalletGroup(null);

        // Create the Wallet, which fails.
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        restWalletMockMvc
            .perform(
                post("/api/wallets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setName(null);

        // Create the Wallet, which fails.
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        restWalletMockMvc
            .perform(
                post("/api/wallets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setAmount(null);

        // Create the Wallet, which fails.
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        restWalletMockMvc
            .perform(
                post("/api/wallets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWallets() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList
        restWalletMockMvc
            .perform(get("/api/wallets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].walletGroup").value(hasItem(DEFAULT_WALLET_GROUP.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get the wallet
        restWalletMockMvc
            .perform(get("/api/wallets/{id}", wallet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wallet.getId().intValue()))
            .andExpect(jsonPath("$.walletGroup").value(DEFAULT_WALLET_GROUP.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingWallet() throws Exception {
        // Get the wallet
        restWalletMockMvc.perform(get("/api/wallets/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Update the wallet
        Wallet updatedWallet = walletRepository.findById(wallet.getId()).get();
        // Disconnect from session so that the updates on updatedWallet are not directly saved in db
        em.detach(updatedWallet);
        updatedWallet
            .walletGroup(UPDATED_WALLET_GROUP)
            .name(UPDATED_NAME)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .remarks(UPDATED_REMARKS);
        WalletDTO walletDTO = walletMapper.toDto(updatedWallet);

        restWalletMockMvc
            .perform(
                put("/api/wallets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isOk());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getWalletGroup()).isEqualTo(UPDATED_WALLET_GROUP);
        assertThat(testWallet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWallet.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void updateNonExistingWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletMockMvc
            .perform(
                put("/api/wallets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWalletWithPatch() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Update the wallet using partial update
        Wallet partialUpdatedWallet = new Wallet();
        partialUpdatedWallet.setId(wallet.getId());

        partialUpdatedWallet.walletGroup(UPDATED_WALLET_GROUP).name(UPDATED_NAME).remarks(UPDATED_REMARKS);

        restWalletMockMvc
            .perform(
                patch("/api/wallets")
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWallet))
            )
            .andExpect(status().isOk());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getWalletGroup()).isEqualTo(UPDATED_WALLET_GROUP);
        assertThat(testWallet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void fullUpdateWalletWithPatch() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Update the wallet using partial update
        Wallet partialUpdatedWallet = new Wallet();
        partialUpdatedWallet.setId(wallet.getId());

        partialUpdatedWallet
            .walletGroup(UPDATED_WALLET_GROUP)
            .name(UPDATED_NAME)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .remarks(UPDATED_REMARKS);

        restWalletMockMvc
            .perform(
                patch("/api/wallets")
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWallet))
            )
            .andExpect(status().isOk());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getWalletGroup()).isEqualTo(UPDATED_WALLET_GROUP);
        assertThat(testWallet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void partialUpdateWalletShouldThrown() throws Exception {
        // Update the wallet without id should throw
        Wallet partialUpdatedWallet = new Wallet();

        restWalletMockMvc
            .perform(
                patch("/api/wallets")
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWallet))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeDelete = walletRepository.findAll().size();

        // Delete the wallet
        restWalletMockMvc
            .perform(delete("/api/wallets/{id}", wallet.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
