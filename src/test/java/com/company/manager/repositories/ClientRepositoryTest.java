package com.company.manager.repositories;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.company.manager.configuration.TestConfig;
import com.company.manager.configuration.TestJPAConfiguration;
import com.company.manager.controllers.params.addressable.client.ClientSearchParams;
import com.company.manager.controllers.params.addressable.client.ClientSearchParams.ClientOrder;
import com.company.manager.models.Client;
import com.company.manager.models.Provider;
import com.company.manager.utils.RandomGenerator;
import com.company.manager.utils.RandomModelCreator;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, TestJPAConfiguration.class},
	loader=AnnotationConfigContextLoader.class)
@Transactional
public class ClientRepositoryTest {
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private ProviderRepository providerRepository;
	@Autowired
	private TestingRepository testingRepository;
	
	@After
	public void tearDown(){
		//clean the database after test run
		testingRepository.wipeTablesData();
	}
	
	@Test
	public void testRetrieveAllClientsNoProviders() {
		List<Client> randomClients = RandomModelCreator.randomEntities(Client.class, 10);
		for(Client randomClient: randomClients){
			randomClient.setId(null);
			randomClient.setProviders(Lists.newArrayList());
			clientRepository.persist(randomClient);
		}
		ClientSearchParams params = new ClientSearchParams();
		params.setFetchProviders(true);
		List<Client> allClients = clientRepository.search(params);
		Assert.assertEquals("When joining with providers, if clients do not have providers, it should return the clients anyway", 10, allClients.size());
	}
	
	@Test
	public void testNoDuplicatesOnFetchJoin(){
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
		ClientSearchParams params = new ClientSearchParams();
		params.setFetchProviders(true);
		List<Client> allClients = clientRepository.search(params);
		Assert.assertEquals("Only one client should have been returned", 1, allClients.size());
		Assert.assertEquals("Client should have two providers", 2, allClients.get(0).getProviders().size());
	}
	
	@Test
	public void testCascade(){
		Provider randomProvider = RandomModelCreator.randomEntity(Provider.class);
		randomProvider.setId(null);
		randomProvider.setClients(Lists.newArrayList());
		providerRepository.persist(randomProvider);
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("542-1235");
		newClient.setProviders(Lists.newArrayList(randomProvider));
		clientRepository.persist(newClient);
		clientRepository.flush();
		providerRepository.detach(randomProvider);
		Long providerId = randomProvider.getId();
		randomProvider = null;
		clientRepository.detach(newClient);
		Long clientId = newClient.getId();
		newClient = null;
		newClient = clientRepository.getById(clientId);
		Assert.assertNotNull("Client should have been retrieved", newClient);
		Assert.assertEquals("Provider's size should match", 1 , newClient.getProviders().size());
		clientRepository.remove(newClient);
		clientRepository.flush();
		newClient = null;
		newClient = clientRepository.getById(clientId);
		Assert.assertNull("Client should have been deleted", newClient);
		randomProvider = providerRepository.getById(providerId);
		Assert.assertNotNull("Provider should not have been deleted", providerId);
	}
	
	
	@Test
	public void testAddProvider() {
		Provider randomProvider = RandomModelCreator.randomEntity(Provider.class);
		randomProvider.setId(null);
		randomProvider.setClients(Lists.newArrayList());
		providerRepository.persist(randomProvider);
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("542-1235");
		clientRepository.persist(newClient);
		Long clientId = newClient.getId();
		clientRepository.detach(newClient);
		newClient = null;
		newClient = clientRepository.getById(clientId);
		newClient.setProviders(Lists.newArrayList(randomProvider));
		clientRepository.persist(newClient);
		clientRepository.flush();
		clientRepository.detach(newClient);
		newClient = null;
		newClient = clientRepository.getById(clientId);
		Assert.assertNotNull("Client should have been retrieved", newClient);
		Assert.assertEquals("Provider's size should match", 1 , newClient.getProviders().size());
		Assert.assertEquals("Provider id should match", randomProvider.getId(), newClient.getProviders().get(0).getId());
	}
	
	@Test
	public void testRemoveProvider() {
		Provider randomProvider = RandomModelCreator.randomEntity(Provider.class);
		randomProvider.setId(null);
		randomProvider.setClients(Lists.newArrayList());
		providerRepository.persist(randomProvider);
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("542-1235");
		newClient.setProviders(Lists.newArrayList(randomProvider));
		clientRepository.persist(newClient);
		clientRepository.flush();
		clientRepository.detach(newClient);
		providerRepository.detach(randomProvider);
		Long clientId = newClient.getId();
		newClient = null;
		newClient = clientRepository.getById(clientId);
		Assert.assertNotNull("Client should have been retrieved", newClient);
		Assert.assertEquals("Provider's size should match", 1 , newClient.getProviders().size());
		clientRepository.detach(newClient);
		newClient = null;
		newClient = clientRepository.getById(clientId);
		newClient.setProviders(Lists.newArrayList());
		clientRepository.persist(newClient);
		clientRepository.flush();
		clientRepository.detach(newClient);
		newClient = null;
		newClient = clientRepository.getById(clientId);
		Assert.assertNotNull("Client should have been retrieved", newClient);
		Assert.assertEquals("Provider's size should match", 0 , newClient.getProviders().size());
		Long providerId = randomProvider.getId();
		randomProvider = null;
		randomProvider = providerRepository.getById(providerId);
		Assert.assertNotNull("Provider should not be null", randomProvider);
	}
	
	@Test
	public void testIdAutogeneration(){
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("542-1235");
		clientRepository.persist(newClient);
		Assert.assertNotNull("When persisting an object, and Id should be autogenerated by the database", newClient.getId());
	}
	
	@Test
	public void testSearch() {
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("123-4567");
		clientRepository.persist(newClient);
		ClientSearchParams params = new ClientSearchParams();
		params.setEmail("bri");
		List<Client> allClients = clientRepository.search(params);
		Assert.assertEquals("When searching by email, like pattern should be applied", 1, allClients.size());
		Assert.assertEquals(newClient.getEmail(), allClients.get(0).getEmail());
		Assert.assertEquals(newClient.getEmail(), allClients.get(0).getEmail());
		Assert.assertEquals(newClient.getName(), allClients.get(0).getName());
		Assert.assertEquals(newClient.getPhone(), allClients.get(0).getPhone());
		params.setEmail(null);
		params.setName("brian");
		allClients = clientRepository.search(params);
		Assert.assertEquals("Search should be case insensitive", 1, allClients.size());
		Assert.assertEquals(newClient.getEmail(), allClients.get(0).getEmail());
		Assert.assertEquals(newClient.getEmail(), allClients.get(0).getEmail());
		Assert.assertEquals(newClient.getName(), allClients.get(0).getName());
		Assert.assertEquals(newClient.getPhone(), allClients.get(0).getPhone());
		params.setEmail("bro");
		params.setName(null);
		allClients = clientRepository.search(params);
		Assert.assertEquals("No match should be found", 0, allClients.size());
		params.setEmail("bri");
		params.setName("wrong");
		allClients = clientRepository.search(params);
		Assert.assertEquals("No match should be found", 0, allClients.size());
		params = new ClientSearchParams();
		params.setId(newClient.getId());
		allClients = clientRepository.search(params);
		Assert.assertEquals("Search should match by id", 1, allClients.size());
		Assert.assertEquals(newClient.getId(), allClients.get(0).getId());
	}
	
	@Test
	public void testSearchPrevious() {
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("09123-12312312");
		clientRepository.persist(newClient);
		Client previous = clientRepository.searchPrevious("bri");
		Assert.assertNull("Search should match exact email", previous);
		previous = clientRepository.searchPrevious("brian@griffin.com");
		Assert.assertNotNull("Search should match exact email", previous);
		Assert.assertEquals(newClient.getEmail(), previous.getEmail());
		Assert.assertEquals(newClient.getEmail(), previous.getEmail());
		Assert.assertEquals(newClient.getName(), previous.getName());
		Assert.assertEquals(newClient.getPhone(), previous.getPhone());
	}
	
	@Test
	public void testAttemptInjection() {
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("9080213123-1321");
		clientRepository.persist(newClient);
		ClientSearchParams params = new ClientSearchParams();
		//will try to break the query thus triggering an exception
		params.setEmail("' FROM");
		List<Client> search = clientRepository.search(params);
		Assert.assertEquals("No match should be found", 0, search.size());
	}
	
	@Test
	public void testEscapeChar() {
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Br!an Griffin");
		newClient.setPhone("582-062012");
		clientRepository.persist(newClient);
		ClientSearchParams params = new ClientSearchParams();
		params.setName("Br!a");
		List<Client> search = clientRepository.search(params);
		Assert.assertEquals("Escape char should be escaped", 1, search.size());
		Assert.assertEquals(newClient.getEmail(), search.get(0).getEmail());
		Assert.assertEquals(newClient.getEmail(), search.get(0).getEmail());
		Assert.assertEquals(newClient.getName(), search.get(0).getName());
		Assert.assertEquals(newClient.getPhone(), search.get(0).getPhone());
		params = new ClientSearchParams();
		params.setName("Br%");
		search = clientRepository.search(params);
		Assert.assertEquals("No match should be found", 0, search.size());
	}
	
	@Test(expected=javax.persistence.PersistenceException.class)
	public void testDuplicatedEmail(){
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		newClient.setPhone("1222-0987654");
		clientRepository.persist(newClient);
		Client otherClient = new Client();
		otherClient.setCreation(new DateTime());
		otherClient.setEmail("brian@griffin.com");
		otherClient.setName("Evil Twin Brian Griffin");
		otherClient.setPhone("0987654-1222");
		clientRepository.persist(otherClient);
	}
	
	@Test(expected=javax.persistence.PersistenceException.class)
	public void testEmailTooLong(){
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		String longEmail = RandomGenerator.randomString(258);
		Assert.assertTrue("generated email should be greated than 256", 256 < longEmail.length());
		newClient.setEmail(longEmail);
		newClient.setName("Brian Griffin");
		newClient.setPhone("1222-0987654");
		clientRepository.persist(newClient);
	}
	
	@Test(expected=javax.persistence.PersistenceException.class)
	public void testNameTooLong(){
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		String longName = RandomGenerator.randomString(82);
		Assert.assertTrue("generated email should be greated than 80", 80 < longName.length());
		newClient.setName(longName);
		newClient.setPhone("1222-0987654");
		clientRepository.persist(newClient);
	}
	
	@Test(expected=javax.persistence.PersistenceException.class)
	public void testPhoneTooLong(){
		Client newClient = new Client();
		newClient.setCreation(new DateTime());
		newClient.setEmail("brian@griffin.com");
		newClient.setName("Brian Griffin");
		String longPhone = RandomGenerator.randomString(52);
		Assert.assertTrue("generated email should be greated than 50", 50 < longPhone.length());
		newClient.setPhone(longPhone);
		clientRepository.persist(newClient);
	}
	
	@Test
	public void testOrder(){
		for(int i = 0; i < 9; i++){
			Client newClient = new Client();
			newClient.setCreation(new DateTime());
			newClient.setEmail("brian"+i+"@griffin.com");
			newClient.setName(Integer.valueOf(9 - i).toString());
			clientRepository.persist(newClient);
		}
		ClientSearchParams searchParams = new ClientSearchParams();
		searchParams.setOrder(ClientOrder.AscName);
		List<Client> orderedClients = clientRepository.search(searchParams);
		Assert.assertEquals("Search should contain all results", 9, orderedClients.size());
		int value = 1;
		for(Client orderedClient : orderedClients){
			Assert.assertEquals("Client should be ordered", Integer.valueOf(value).toString(), orderedClient.getName());
			value++;
		}
	}
	
	@Test
	public void testRetrieveAllClientsEmpty() {
		ClientSearchParams params = new ClientSearchParams();
		params.setFetchProviders(true);
		List<Client> allClients = clientRepository.search(params);
		Assert.assertEquals("If no clients, the query should return an empty list", 0, allClients.size());
	}
}
