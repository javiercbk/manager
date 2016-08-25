package com.company.manager.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.net.URLEncoder;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.company.manager.configuration.TestConfig;
import com.company.manager.configuration.TestJPAConfiguration;
import com.company.manager.controllers.params.addressable.client.ClientProspect;
import com.company.manager.controllers.params.patch.OperationType;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.controllers.params.patch.Patches;
import com.company.manager.models.Client;
import com.company.manager.models.Provider;
import com.company.manager.repositories.ClientRepository;
import com.company.manager.repositories.ProviderRepository;
import com.company.manager.repositories.TestingRepository;
import com.company.manager.utils.RandomGenerator;
import com.company.manager.utils.RandomModelCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfig.class,
		TestJPAConfiguration.class }, loader = AnnotationConfigContextLoader.class)
@Transactional
public class ClientControllerTest {
	@Autowired
	private ClientController clientController;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private ProviderRepository providerRepository;
	@Autowired
	private TestingRepository testingRepository;
	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(clientController).build();
	}

	@After
	public void tearDown() {
		// clean the database after test run
		testingRepository.wipeTablesData();
	}

	
	@Test
	public void getClients() throws Exception{
		mockMvc.perform(get("/api/clients"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", Matchers.hasSize(0)));
		//add some clients
		List<Client> randomClients = RandomModelCreator.randomEntities(Client.class, 10);
		List<Long> ids = Lists.newArrayList();
		List<String> emails = Lists.newArrayList();
		List<String> phones = Lists.newArrayList();
		for(Client randomClient: randomClients){
			randomClient.setId(null);
			randomClient.setProviders(Lists.newArrayList());
			clientRepository.persist(randomClient);
			ids.add(randomClient.getId());
			emails.add(randomClient.getEmail());
			phones.add(randomClient.getPhone());
		}
		ResultActions expectations = mockMvc.perform(get("/api/clients"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", Matchers.hasSize(10)));
		for(int i = 0; i < 10; i++){
			expectations.andExpect(jsonPath("$["+i+"].id", Matchers.equalTo(ids.get(i).intValue())))
			.andExpect(jsonPath("$["+i+"].email", Matchers.equalTo(emails.get(i))))
			.andExpect(jsonPath("$["+i+"].phone", Matchers.equalTo(phones.get(i))));
		}
	}
	
	@Test
	public void getClientsMultipleProvidersNoDuplicatedResult() throws Exception{
		Provider provider1 = new Provider();
		provider1.setName("Provider 1");
		providerRepository.persist(provider1);
		providerRepository.flush();
		Provider provider2 = new Provider();
		provider2.setName("Provider 2");
		providerRepository.persist(provider2);
		providerRepository.flush();
		Client client = new Client();
		client.setEmail("brian@griffin.com");
		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		client.setProviders(Lists.newArrayList(provider1, provider2));
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		mockMvc.perform(get("/api/clients"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", Matchers.hasSize(1)));
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void invalidPatchClient() throws Exception{
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		//patch with no op
		Patch<String> patch = new Patch<String>();
		patch.setValue("New Brian Griffin");
		patch.setPath("/name");
		List<Patch> patches = Lists.newArrayList(patch);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchNonExistingClient() throws Exception{
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Patch<Object> patch = new Patch<Object>();
		patch.setValue("New Brian Griffin");
		patch.setPath("/name");
		patch.setOp(OperationType.Replace);
		List<Patch> patches = Lists.newArrayList(patch);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/123").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(content().string(Matchers.equalTo("")))
		.andExpect(status().isNotFound());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClientWithExisitingEmail() throws Exception{
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Client otherClient = new Client();
		otherClient.setEmail("otherBrian@griffin.com");

		otherClient.setPhone("666-6666");
		otherClient.setName("Other Brian Griffin");
		clientRepository.persist(otherClient);
		clientRepository.flush();
		clientRepository.detach(otherClient);
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue(otherClient.getEmail());
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isUnprocessableEntity());
		Client sameClient = clientRepository.getById(client.getId());
		Assert.assertEquals("Name should not have been changed", client.getName(), sameClient.getName());
		Assert.assertEquals("Email should not have been changed", client.getEmail(), sameClient.getEmail());
		Assert.assertEquals("Phone should not have been changed", client.getPhone(), sameClient.getPhone());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClient() throws Exception{
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue("brian1@griffin.com");
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isNoContent());
		Long clientId = client.getId();
		client = null;
		client = clientRepository.getById(clientId);
		Assert.assertEquals("Name should have been changed", patchName.getValue(), client.getName());
		Assert.assertEquals("Email should have been changed", patchEmail.getValue(), client.getEmail());
		Assert.assertEquals("Phone should have been changed", patchPhone.getValue(), client.getPhone());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClientAddProvider() throws Exception{
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Provider provider = new Provider();
		provider.setName("Proven Provider");

		providerRepository.persist(provider);
		providerRepository.flush();
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue("brian1@griffin.com");
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		Patch<Long> patchAddProvider = new Patch<Long>();
		patchAddProvider.setValue(provider.getId());
		patchAddProvider.setPath("/providers");
		patchAddProvider.setOp(OperationType.Add);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail, patchAddProvider);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isNoContent());
		Long clientId = client.getId();
		client = null;
		client = clientRepository.getById(clientId);
		Assert.assertEquals("Name should have been changed", patchName.getValue(), client.getName());
		Assert.assertEquals("Email should have been changed", patchEmail.getValue(), client.getEmail());
		Assert.assertEquals("Phone should have been changed", patchPhone.getValue(), client.getPhone());
		Assert.assertEquals("Providers should have been changed", 1, client.getProviders().size());
		Assert.assertEquals("Providers should have been changed", provider.getId(), client.getProviders().get(0).getId());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClientReplaceNonExistingProvider() throws Exception{
		Provider provider = new Provider();
		provider.setName("Proven Provider");

		providerRepository.persist(provider);
		providerRepository.flush();
		providerRepository.flush();
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		client.setProviders(Lists.newArrayList(provider));
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue("brian1@griffin.com");
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		Patch<Long> patchReplaceProvider = new Patch<Long>();
		patchReplaceProvider.setValue(Long.valueOf(123l));
		patchReplaceProvider.setPath("/providers");
		patchReplaceProvider.setOp(OperationType.Replace);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail, patchReplaceProvider);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isUnprocessableEntity());
		Long clientId = client.getId();
		Client sameClient = clientRepository.getById(clientId);
		Assert.assertEquals("Name should not have been changed", client.getName(), sameClient.getName());
		Assert.assertEquals("Email should not have been changed", client.getEmail(), sameClient.getEmail());
		Assert.assertEquals("Phone should not have been changed", client.getPhone(), sameClient.getPhone());
		Assert.assertEquals("Providers size should not have changed", 1, sameClient.getProviders().size());
		Assert.assertEquals("Providers should not have been changed", client.getProviders().get(0).getId(), sameClient.getProviders().get(0).getId());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClientAddNonExistingProvider() throws Exception{
		Provider provider = new Provider();
		provider.setName("Proven Provider");

		providerRepository.persist(provider);
		providerRepository.flush();
		providerRepository.flush();
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		client.setProviders(Lists.newArrayList(provider));
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue("brian1@griffin.com");
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		Patch<Long> patchReplaceProvider = new Patch<Long>();
		patchReplaceProvider.setValue(Long.valueOf(123l));
		patchReplaceProvider.setPath("/providers");
		patchReplaceProvider.setOp(OperationType.Add);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail, patchReplaceProvider);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isUnprocessableEntity());
		Long clientId = client.getId();
		Client sameClient = clientRepository.getById(clientId);
		Assert.assertEquals("Name should not have been changed", client.getName(), sameClient.getName());
		Assert.assertEquals("Email should not have been changed", client.getEmail(), sameClient.getEmail());
		Assert.assertEquals("Phone should not have been changed", client.getPhone(), sameClient.getPhone());
		Assert.assertEquals("Providers size should not have changed", 1, sameClient.getProviders().size());
		Assert.assertEquals("Providers should not have been changed", client.getProviders().get(0).getId(), sameClient.getProviders().get(0).getId());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClientRemoveNonExistingProvider() throws Exception{
		Provider provider = new Provider();
		provider.setName("Proven Provider");

		providerRepository.persist(provider);
		providerRepository.flush();
		providerRepository.flush();
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		client.setProviders(Lists.newArrayList(provider));
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue("brian1@griffin.com");
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		Patch<Long> patchReplaceProvider = new Patch<Long>();
		patchReplaceProvider.setPath("/providers/123");
		patchReplaceProvider.setOp(OperationType.Remove);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail, patchReplaceProvider);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isUnprocessableEntity());
		Long clientId = client.getId();
		Client sameClient = clientRepository.getById(clientId);
		Assert.assertEquals("Name should not have been changed", client.getName(), sameClient.getName());
		Assert.assertEquals("Email should not have been changed", client.getEmail(), sameClient.getEmail());
		Assert.assertEquals("Phone should not have been changed", client.getPhone(), sameClient.getPhone());
		Assert.assertEquals("Providers size should not have changed", 1, sameClient.getProviders().size());
		Assert.assertEquals("Providers should not have been changed", client.getProviders().get(0).getId(), sameClient.getProviders().get(0).getId());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClientReplaceProvider() throws Exception{
		Provider provider = new Provider();
		provider.setName("Proven Provider");

		providerRepository.persist(provider);
		providerRepository.flush();
		Provider anotherProvider = new Provider();
		anotherProvider.setName("Another Proven Provider");

		providerRepository.persist(anotherProvider);
		providerRepository.flush();
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		client.setProviders(Lists.newArrayList(provider));
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue("brian1@griffin.com");
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		Patch<Long> patchReplaceProvider = new Patch<Long>();
		patchReplaceProvider.setValue(anotherProvider.getId());
		patchReplaceProvider.setPath("/providers");
		patchReplaceProvider.setOp(OperationType.Replace);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail, patchReplaceProvider);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isNoContent());
		Long clientId = client.getId();
		client = null;
		client = clientRepository.getById(clientId);
		Assert.assertEquals("Name should have been changed", patchName.getValue(), client.getName());
		Assert.assertEquals("Email should have been changed", patchEmail.getValue(), client.getEmail());
		Assert.assertEquals("Phone should have been changed", patchPhone.getValue(), client.getPhone());
		Assert.assertEquals("Providers size should be the same", 1, client.getProviders().size());
		Assert.assertEquals("Providers should have been changed", anotherProvider.getId(), client.getProviders().get(0).getId());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void patchClientRemoveProvider() throws Exception{
		Provider provider = new Provider();
		provider.setName("Proven Provider");

		providerRepository.persist(provider);
		providerRepository.flush();
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		client.setProviders(Lists.newArrayList(provider));
		clientRepository.persist(client);
		clientRepository.flush();
		clientRepository.detach(client);
		Patch<String> patchName = new Patch<String>();
		patchName.setValue("New Brian Griffin");
		patchName.setPath("/name");
		patchName.setOp(OperationType.Replace);
		Patch<String> patchPhone = new Patch<String>();
		patchPhone.setValue("666-6666");
		patchPhone.setPath("/phone");
		patchPhone.setOp(OperationType.Replace);
		Patch<String> patchEmail = new Patch<String>();
		patchEmail.setValue("brian1@griffin.com");
		patchEmail.setPath("/email");
		patchEmail.setOp(OperationType.Replace);
		Patch<Long> patchRemoveProvider = new Patch<Long>();
		patchRemoveProvider.setPath("/providers/" + provider.getId());
		patchRemoveProvider.setOp(OperationType.Remove);
		List<Patch> patches = Lists.newArrayList(patchName, patchPhone, patchEmail, patchRemoveProvider);
		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(patch("/api/clients/" + client.getId()).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(new Patches(patches))))
		.andExpect(status().isNoContent());
		Long clientId = client.getId();
		client = null;
		client = clientRepository.getById(clientId);
		Assert.assertEquals("Name should have been changed", patchName.getValue(), client.getName());
		Assert.assertEquals("Email should have been changed", patchEmail.getValue(), client.getEmail());
		Assert.assertEquals("Phone should have been changed", patchPhone.getValue(), client.getPhone());
		Assert.assertEquals("Providers size should have changed", 0, client.getProviders().size());
	}
	
	@Test
	public void deleteNonExistingClient() throws Exception{
		mockMvc.perform(delete("/api/clients/123"))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteClient() throws Exception{
		Client client = new Client();
		client.setEmail("brian@griffin.com");

		client.setPhone("555-5555");
		client.setName("Brian Griffin");
		clientRepository.persist(client);
		mockMvc.perform(delete("/api/clients/" + client.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", Matchers.equalTo(Integer.valueOf(client.getId().intValue()))))
		.andExpect(jsonPath("$.email", Matchers.equalTo(client.getEmail())))
		.andExpect(jsonPath("$.name", Matchers.equalTo(client.getName())))
		.andExpect(jsonPath("$.phone", Matchers.equalTo(client.getPhone())));
	}
	
	@Test
	public void getClientsWithInvalidFilters() throws Exception{
		mockMvc.perform(get("/api/clients?email="))
		.andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/clients?name="))
		.andExpect(status().isBadRequest());
		//name too long
		mockMvc.perform(get("/api/clients?name=" + URLEncoder.encode(RandomGenerator.randomString(100), "UTF-8")))
		.andExpect(status().isBadRequest());
		//email too long
		mockMvc.perform(get("/api/clients?email=" + URLEncoder.encode(RandomGenerator.randomString(270), "UTF-8")))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void postClient() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ClientProspect clientProspect = new ClientProspect();
		clientProspect.setName("Brian Griffin");
		clientProspect.setEmail("brian@griffin.com");
		clientProspect.setPhone("3891371298379123-129831273");
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", Matchers.equalTo(clientProspect.getName())))
		.andExpect(jsonPath("$.email", Matchers.equalTo(clientProspect.getEmail())))
		.andExpect(jsonPath("$.phone", Matchers.equalTo(clientProspect.getPhone())));
		List<Client> clients = clientRepository.search(null);
		Assert.assertEquals("There should be 1 client only", 1, clients.size());
		Assert.assertEquals(clientProspect.getName(), clients.get(0).getName());
		Assert.assertEquals(clientProspect.getEmail(), clients.get(0).getEmail());
		Assert.assertEquals(clientProspect.getPhone(), clients.get(0).getPhone());
	}
	
	@Test
	public void postClientWithProvider() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ClientProspect clientProspect = new ClientProspect();
		Provider provider = new Provider();
		provider.setName("Proven Provider");

		providerRepository.persist(provider);
		providerRepository.flush();
		clientProspect.setName("Brian Griffin");
		clientProspect.setEmail("brian@griffin.com");
		clientProspect.setPhone("555-5555");
		clientProspect.setProviders(Lists.newArrayList(provider.getId()));
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", Matchers.equalTo(clientProspect.getName())))
		.andExpect(jsonPath("$.email", Matchers.equalTo(clientProspect.getEmail())))
		.andExpect(jsonPath("$.providers", Matchers.hasSize(1)))
		.andExpect(jsonPath("$.providers[0].id", Matchers.equalTo(Integer.valueOf(provider.getId().intValue()))));
		List<Client> clients = clientRepository.search(null);
		Assert.assertEquals("There should be 1 client only", 1, clients.size());
		Assert.assertEquals(clientProspect.getName(), clients.get(0).getName());
		Assert.assertEquals(clientProspect.getEmail(), clients.get(0).getEmail());
		Assert.assertEquals(clientProspect.getPhone(), clients.get(0).getPhone());
		Assert.assertNotNull("Retrieved client should contain a provider", clients.get(0).getProviders());
		Assert.assertEquals("There should be 1 provider", 1, clients.get(0).getProviders().size());
		Assert.assertEquals("Provider Id should match", provider.getId(), clients.get(0).getProviders().get(0).getId());
	}
	
	@Test
	public void postClientWithNonExistingProvider() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ClientProspect clientProspect = new ClientProspect();
		clientProspect.setName("Brian Griffin");
		clientProspect.setEmail("brian@griffin.com");
		clientProspect.setPhone("555-5555");
		//magical non existing provider id
		clientProspect.setProviders(Lists.newArrayList(Long.valueOf(123)));
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void postInvalidClient() throws Exception{
		//empty post
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(""))
		.andExpect(status().isBadRequest());
		//empty prospect
		ObjectMapper mapper = new ObjectMapper();
		ClientProspect clientProspect = new ClientProspect();
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isBadRequest());
		//no email prospect
		clientProspect = new ClientProspect();
		clientProspect.setName("Brian");
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isBadRequest());
		//no name prospect
		clientProspect = new ClientProspect();
		clientProspect.setEmail("brian@griffin.com");
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isBadRequest());
		//malformed email
		clientProspect = new ClientProspect();
		clientProspect.setName("Brian Griffin");
		clientProspect.setEmail("briangriffin.com");
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isBadRequest());
		//phone too long
		clientProspect = new ClientProspect();
		clientProspect.setName("Brian Griffin");
		clientProspect.setEmail("brian@griffin.com");
		clientProspect.setPhone(RandomGenerator.randomString(80));
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isBadRequest());
		//email too long
		clientProspect = new ClientProspect();
		clientProspect.setName("Brian Griffin");
		StringBuilder stringBuilder = new StringBuilder("bri");
		for(int i = 0; i < 254; i++){
			stringBuilder.append("a");
		}
		stringBuilder.append("n");
		clientProspect.setEmail(stringBuilder.toString());
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isBadRequest());
		//name too long
		clientProspect = new ClientProspect();
		clientProspect.setName(RandomGenerator.randomString(120));
		clientProspect.setEmail("brian@griffin.com");
		mockMvc.perform(post("/api/clients").
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(clientProspect)))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void getClientsWithFilters() throws Exception{
		Client broClient = new Client();
		broClient.setEmail("bro@griffin.com");

		broClient.setPhone("555-5555");
		broClient.setName("Bro Griffin");
		clientRepository.persist(broClient);
		Client briClient = new Client();
		briClient.setEmail("bri@griffin.com");

		briClient.setPhone("111-1111");
		briClient.setName("Bri Griffin");
		clientRepository.persist(briClient);
		mockMvc.perform(get("/api/clients"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
		mockMvc.perform(get("/api/clients?email=br"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
		mockMvc.perform(get("/api/clients?name=br"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
		mockMvc.perform(get("/api/clients?name=bri"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].id", Matchers.equalTo(briClient.getId().intValue())));;
		mockMvc.perform(get("/api/clients?email=bro"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].id", Matchers.equalTo(broClient.getId().intValue())));
		mockMvc.perform(get("/api/clients?name=bri&email=bro"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(0)));
		mockMvc.perform(get("/api/clients?name=bro&email=bro"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].id", Matchers.equalTo(broClient.getId().intValue())));
		mockMvc.perform(get("/api/clients?limit=1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
	}
}
