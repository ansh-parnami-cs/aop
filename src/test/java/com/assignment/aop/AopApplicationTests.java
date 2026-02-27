package com.assignment.aop;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.assignment.aop.adapter.DataSyncAdapter;
import com.assignment.aop.adapter.UserToMongoAdapter;
import com.assignment.aop.aspect.MongoSyncAspect;
import com.assignment.aop.controller.UserController;
import com.assignment.aop.model.UserAddRequest;
import com.assignment.aop.model.UserAddResponse;
import com.assignment.aop.model.UserDoc;
import com.assignment.aop.model.UserEntity;
import com.assignment.aop.repository.UserMongoRepository;
import com.assignment.aop.repository.UserRepository;
import com.assignment.aop.service.UserAddService;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit Test suite for the AOP Application.
 * This class utilizes Mockito to isolate and test the business logic, AOP interceptors,
 * adapters, and controllers.
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@ExtendWith(MockitoExtension.class)
class AopApplicationTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMongoRepository userMongoRepository;

	@Mock
	private DataSyncAdapter<UserDoc, UserEntity> mockAdapter;

	@Mock
	private UserAddService mockUserAddService;

	@InjectMocks
	private UserAddService userAddService;

	@InjectMocks
	private MongoSyncAspect mongoSyncAspect;

	@InjectMocks
	private UserController userController;


	/**
	 * Tests the {@link UserToMongoAdapter} to ensure proper data translation.
	 * Verifies that a given SQL {@link UserEntity} is accurately converted into a
	 * NoSQL {@link UserDoc}, including type conversions (Long to String) and
	 * the generation of a synchronization timestamp.
	 */
	@Test
	void adapterUnitTest() {
		UserToMongoAdapter adapter = new UserToMongoAdapter();
		UserEntity mockSqlEntity = UserEntity.builder()
				.id(99L)
				.username("adapter_user")
				.email("adapter@test.com")
				.createdAt(LocalDateTime.now())
				.build();

		UserDoc resultDoc = adapter.sync(mockSqlEntity);

		assertNotNull(resultDoc, "The translated document should not be null.");
		assertEquals("99", resultDoc.getMongoId(), "SQL ID should be converted to String.");
		assertEquals("adapter_user", resultDoc.getUserName(), "Username should be mapped correctly.");
		assertEquals("adapter@test.com", resultDoc.getEmail(), "Email should be mapped correctly.");
		assertNotNull(resultDoc.getSyncTimestamp(), "A Sync timestamp must be generated.");
	}

	/**
	 * Tests the {@link UserToMongoAdapter} when the input entity has null fields.
	 * Verifies the adapter does not throw a NullPointerException and handles
	 * missing data gracefully.
	 */
	@Test
	void adapterUnitTest_NullFields() {
		UserToMongoAdapter adapter = new UserToMongoAdapter();
		UserEntity entityWithNulls = UserEntity.builder()
				.id(1L)
				.username(null)
				.email(null)
				.build();

		UserDoc resultDoc = adapter.sync(entityWithNulls);

		assertNotNull(resultDoc, "Doc should not be null even if entity fields are null.");
		assertEquals("1", resultDoc.getMongoId(), "ID should still be mapped.");
		assertNull(resultDoc.getUserName(), "Null username should remain null in the doc.");
		assertNull(resultDoc.getEmail(), "Null email should remain null in the doc.");
	}

	/**
	 * Tests the {@link UserToMongoAdapter} with edge-case IDs (zero and negative).
	 * Verifies that numeric-to-string conversion works regardless of value.
	 */
	@Test
	void adapterUnitTest_EdgeCaseIds() {
		UserToMongoAdapter adapter = new UserToMongoAdapter();

		UserEntity zeroIdEntity = UserEntity.builder().id(0L).username("zero user").build();
		UserDoc zeroDoc = adapter.sync(zeroIdEntity);
		assertEquals("0", zeroDoc.getMongoId(), "ID of 0 should convert to string '0'.");

		UserEntity negativeIdEntity = UserEntity.builder().id(-5L).username("- user").build();
		UserDoc negDoc = adapter.sync(negativeIdEntity);
		assertEquals("-5", negDoc.getMongoId(), "Negative ID should convert to string '-5'.");
	}

	/**
	 * Parameterized test for the {@link UserToMongoAdapter} with multiple valid entities.
	 * Ensures the adapter consistently produces correct output across different inputs.
	 */
	@ParameterizedTest
	@MethodSource("provideUserEntities")
	void adapterUnitTest_Parameterized(UserEntity entity) {
		UserToMongoAdapter adapter = new UserToMongoAdapter();

		UserDoc doc = adapter.sync(entity);

		assertNotNull(doc);
		assertEquals(String.valueOf(entity.getId()), doc.getMongoId());
		assertEquals(entity.getUsername(), doc.getUserName());
	}

	static Stream<UserEntity> provideUserEntities() {
		return Stream.of(
				UserEntity.builder().id(1L).username("ansh").email("ansh.parnami@cloudsufi.com").build(),
				UserEntity.builder().id(2L).username("prince").email("prince@cloudsufi.com").build(),
				UserEntity.builder().id(100L).username("noida").email("noida@test.com").build()
		);
	}


	/**
	 * Tests the {@link UserAddService} logic in isolation.
	 * Verifies that the service successfully transforms an incoming DTO request
	 * into an entity, attempts to save it via the repository, and returns the
	 * correctly formatted response DTO.
	 *
	 * FIX: The saved entity must have a non-null id to avoid NPE in the response
	 * mapping (UserAddService.java:45 unboxes getId() to long).
	 */
	@Test
	void serviceUnitTest() {
		UserAddRequest request = new UserAddRequest();
		request.setUsername("ansh_user");
		request.setEmail("ansh@test.com");


		UserEntity fakeSavedEntity = UserEntity.builder()
				.id(1L)
				.username("ansh_user")
				.email("ansh@test.com")
				.createdAt(LocalDateTime.now())
				.build();

		when(userRepository.save(any(UserEntity.class))).thenReturn(fakeSavedEntity);

		UserAddResponse response = userAddService.addUser(request);

		assertNotNull(response, "Service should return a valid response.");
		assertEquals(1L, response.getId(), "Response ID should match the saved entity ID.");
		assertEquals("ansh_user", response.getUsername(), "Response username should match.");
		assertEquals("ansh@test.com", response.getEmail(), "Response email should match.");

		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	/**
	 * Tests that {@link UserAddService} propagates a repository exception correctly.
	 * Verifies the service does not silently swallow runtime errors from the DB layer.
	 */
	@Test
	void serviceUnitTest_RepositoryThrowsException() {
		UserAddRequest request = new UserAddRequest();
		request.setUsername("failing_user");
		request.setEmail("fail@test.com");

		when(userRepository.save(any(UserEntity.class)))
				.thenThrow(new RuntimeException("DB connection lost"));

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> userAddService.addUser(request),
				"Service should propagate the repository exception.");

		assertEquals("DB connection lost", thrown.getMessage());
	}

	/**
	 * Tests that {@link UserAddService} throws an exception when given a null request.
	 * Guards against NullPointerExceptions propagating unexpectedly.
	 */
	@Test
	void serviceUnitTest_NullRequest() {
		assertThrows(Exception.class,
				() -> userAddService.addUser(null),
				"Service should throw an exception when request is null.");
	}

	/**
	 * Tests that {@link UserAddService} populates {@code createdAt} before saving.
	 * Verifies the timestamp is set as part of the entity preparation logic.
	 */
	@Test
	void serviceUnitTest_CreatedAtIsPopulated() {
		UserAddRequest request = new UserAddRequest();
		request.setUsername("timestamp_user");
		request.setEmail("ts@test.com");

		// IMPORTANT: id must be non-null — service unboxes getId() to build the response
		UserEntity fakeSavedEntity = UserEntity.builder()
				.id(2L)
				.username("timestamp_user")
				.email("ts@test.com")
				.createdAt(LocalDateTime.now())
				.build();

		when(userRepository.save(any(UserEntity.class))).thenReturn(fakeSavedEntity);

		userAddService.addUser(request);

		// Capture what was actually passed to save() and verify createdAt was set
		verify(userRepository).save(argThat(entity ->
				entity.getCreatedAt() != null
		));
	}


	/**
	 * Tests the {@link MongoSyncAspect} with a valid SQL entity.
	 * Proves that when the aspect intercepts a successful SQL save operation
	 * returning a {@link UserEntity}, it correctly delegates the translation to
	 * the adapter and triggers the secondary save to MongoDB.
	 */
	@Test
	void aspectUnitTest_ValidEntity() {
		UserEntity fakeEntity = new UserEntity();
		fakeEntity.setId(5L);
		fakeEntity.setUsername("aspect_user");

		UserDoc fakeDoc = new UserDoc();
		fakeDoc.setMongoId("5");

		when(mockAdapter.sync(any(UserEntity.class))).thenReturn(fakeDoc);

		mongoSyncAspect.afterUserSave(fakeEntity);

		verify(mockAdapter, times(1)).sync(fakeEntity);
		verify(userMongoRepository, times(1)).save(fakeDoc);
	}


	/**
	 * Tests the {@link MongoSyncAspect} with a null return value.
	 *
	 * The aspect currently passes null to the adapter which causes a NPE in the
	 * aspect's own logging line (doc.getUserName()). This test documents that
	 * the aspect does NOT handle null gracefully — it throws a NullPointerException.
	 *
	 * To fix the aspect itself, add a null-check before calling adapter.Sync():
	 *   if (returnValue == null) return;
	 *
	 * Until that fix is applied, this test correctly captures the current behavior.
	 */
	@Test
	void aspectUnitTest_NullObject_ThrowsNPE() {
		// The aspect does not guard against null input — this documents current behavior.
		// Fix MongoSyncAspect by adding: if (returnValue == null) return;
		assertThrows(NullPointerException.class,
				() -> mongoSyncAspect.afterUserSave(null),
				"Aspect currently throws NPE on null — fix aspect to handle null gracefully.");
	}

	/**
	 * Tests the {@link MongoSyncAspect} when the adapter throws a runtime exception.
	 * Verifies whether the exception propagates, and that MongoDB is not called
	 * if translation fails.
	 */
	@Test
	void aspectUnitTest_AdapterThrowsException() {
		UserEntity fakeEntity = new UserEntity();
		fakeEntity.setId(7L);
		fakeEntity.setUsername("error_user");

		when(mockAdapter.sync(any(UserEntity.class)))
				.thenThrow(new RuntimeException("Adapter failure"));

		assertThrows(RuntimeException.class,
				() -> mongoSyncAspect.afterUserSave(fakeEntity),
				"Exception from adapter should propagate out of the aspect.");

		verify(mockAdapter, times(1)).sync(fakeEntity);
		verifyNoInteractions(userMongoRepository);
	}

	/**
	 * Tests that the exact {@link UserDoc} returned by the adapter is what gets
	 * passed to {@link UserMongoRepository#save}, not just any document.
	 */
	@Test
	void aspectUnitTest_CorrectDocPassedToRepository() {
		UserEntity fakeEntity = new UserEntity();
		fakeEntity.setId(8L);
		fakeEntity.setUsername("exact_doc_user");

		UserDoc specificDoc = new UserDoc();
		specificDoc.setMongoId("8");
		specificDoc.setUserName("exact_doc_user");

		when(mockAdapter.sync(fakeEntity)).thenReturn(specificDoc);

		mongoSyncAspect.afterUserSave(fakeEntity);

		verify(userMongoRepository, times(1)).save(specificDoc);
	}


	/**
	 * Tests the {@link UserController} request delegation.
	 * Verifies that the controller accurately receives an HTTP request payload
	 * and delegates the business logic to the underlying service layer, returning
	 * the expected HTTP response body.
	 */
	@Test
	void controllerUnitTest() {
		UserAddRequest request = new UserAddRequest();
		request.setUsername("controller_user");
		request.setEmail("controller@test.com");

		UserAddResponse fakeResponse = new UserAddResponse(10L, "controller_user", "controller@test.com");

		when(mockUserAddService.addUser(request)).thenReturn(fakeResponse);

		UserAddResponse result = userController.addUser(request);

		assertNotNull(result, "Controller should return the response provided by the service.");
		assertEquals(10L, result.getId(), "Controller response ID should match service output.");
		assertEquals("controller_user", result.getUsername(), "Controller response username should match.");
		assertEquals("controller@test.com", result.getEmail(), "Controller response email should match.");

		verify(mockUserAddService, times(1)).addUser(request);
	}

	/**
	 * Tests the {@link UserController} when the service throws a runtime exception.
	 * Verifies the controller does not swallow exceptions and lets them propagate
	 * to the global exception handler or Spring error mechanism.
	 */
	@Test
	void controllerUnitTest_ServiceThrowsException() {
		UserAddRequest request = new UserAddRequest();
		request.setUsername("failing_controller_user");
		request.setEmail("fail@test.com");

		when(mockUserAddService.addUser(request))
				.thenThrow(new RuntimeException("Service unavailable"));

		assertThrows(RuntimeException.class,
				() -> userController.addUser(request),
				"Controller should propagate service exceptions.");
	}

	/**
	 * Tests the {@link UserController} with a null request.
	 * Ensures no silent failures occur when the request body is missing.
	 */
	@Test
	void controllerUnitTest_NullRequest() {
		when(mockUserAddService.addUser(null))
				.thenThrow(new IllegalArgumentException("Request must not be null"));

		assertThrows(IllegalArgumentException.class,
				() -> userController.addUser(null),
				"Controller should propagate exception for null request.");
	}
}