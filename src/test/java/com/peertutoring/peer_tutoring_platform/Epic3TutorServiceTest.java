package com.peertutoring.peer_tutoring_platform;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.exception.DuplicateResourceException;
import com.peertutoring.exception.ResourceNotFoundException;
import com.peertutoring.model.*;
import com.peertutoring.pattern.factory.SessionFactory;
import com.peertutoring.pattern.observer.SessionEventPublisher;
import com.peertutoring.pattern.strategy.EligibilityStrategy;
import com.peertutoring.repository.*;
import com.peertutoring.service.impl.TutorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ══════════════════════════════════════════════════════════════
 * EPIC 3 Unit Tests — TutorServiceImpl
 * ══════════════════════════════════════════════════════════════
 *
 * Tests cover all 5 user stories:
 *   1. Register as Tutor
 *   2. Check Eligibility
 *   3. Create Session
 *   4. View My Sessions
 *   5. Update Session
 *
 * Uses Mockito to mock all dependencies — pure unit tests.
 * No Spring context needed — tests run fast.
 *
 * HOW TO RUN:
 *   mvn test
 * ══════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
class Epic3TutorServiceTest {

    // ── Mocked dependencies ───────────────────────────────────────────────────
    @Mock private TutorProfileRepository tutorProfileRepo;
    @Mock private TutoringSessionRepository sessionRepo;
    @Mock private UserRepository userRepo;
    @Mock private EligibilityStrategy eligibilityStrategy;
    @Mock private SessionFactory sessionFactory;
    @Mock private SessionEventPublisher eventPublisher;

    @InjectMocks
    private TutorServiceImpl tutorService;

    // ── Test data ─────────────────────────────────────────────────────────────
    private User studentUser;
    private User tutorUser;
    private TutorProfile tutorProfile;
    private TutoringSession session;
    private RegisterAsTutorRequest registerRequest;
    private CreateSessionRequest sessionRequest;

    @BeforeEach
    void setUp() {
        // Create a STUDENT user
        studentUser = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@test.com")
                .password("password123")
                .role(User.Role.STUDENT)
                .points(60)  // above 50 threshold
                .build();

        // Create a TUTOR user
        tutorUser = User.builder()
                .id(2L)
                .name("Bob")
                .email("bob@test.com")
                .password("password123")
                .role(User.Role.TUTOR)
                .points(60)
                .build();

        // TutorProfile
        tutorProfile = TutorProfile.builder()
                .id(1L)
                .user(studentUser)
                .gpa(3.5)
                .rating(0.0)
                .bio("Expert in DSA")
                .approved(false)
                .build();

        // TutoringSession
        session = TutoringSession.builder()
                .id(1L)
                .topic("Binary Trees")
                .subject(Question.Subject.DATA_STRUCTURES)
                .scheduledAt(LocalDateTime.now().plusDays(3))
                .capacity(10)
                .description("Learn traversal algorithms")
                .tutor(tutorUser)
                .status(TutoringSession.SessionStatus.UPCOMING)
                .build();

        // Request DTOs
        registerRequest = RegisterAsTutorRequest.builder()
                .gpa(3.5)
                .bio("Expert in DSA")
                .build();

        sessionRequest = CreateSessionRequest.builder()
                .topic("Binary Trees")
                .subject(Question.Subject.DATA_STRUCTURES)
                .scheduledAt(LocalDateTime.now().plusDays(3))
                .capacity(10)
                .description("Learn traversal algorithms")
                .build();

        // Register service with empty listener list for tests
        tutorService = new TutorServiceImpl(
                tutorProfileRepo, sessionRepo, userRepo,
                eligibilityStrategy, sessionFactory, eventPublisher,
                Collections.emptyList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STORY 1 TESTS: Register as Tutor
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void registerAsTutor_success_studentBecomesToutor() {
        // Arrange
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(studentUser));
        when(tutorProfileRepo.existsByUser(studentUser)).thenReturn(false);
        when(tutorProfileRepo.save(any(TutorProfile.class))).thenReturn(tutorProfile);

        // Act
        TutorProfileResponse response = tutorService.registerAsTutor(registerRequest, "alice@test.com");

        // Assert
        assertNotNull(response);
        assertEquals("Alice", response.getTutorName());
        assertEquals(3.5, response.getGpa());
        // Verify role was upgraded to TUTOR
        verify(userRepo, times(1)).save(argThat(u -> u.getRole() == User.Role.TUTOR));
        // Verify profile was saved
        verify(tutorProfileRepo, times(1)).save(any(TutorProfile.class));
        System.out.println("✅ TEST PASSED: Student successfully registered as tutor");
    }

    @Test
    void registerAsTutor_throwsException_whenAlreadyTutor() {
        // Arrange — user is already a TUTOR
        tutorUser.setRole(User.Role.TUTOR);
        when(userRepo.findByEmail("bob@test.com")).thenReturn(Optional.of(tutorUser));

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> tutorService.registerAsTutor(registerRequest, "bob@test.com"));
        System.out.println("✅ TEST PASSED: Cannot register twice as tutor");
    }

    @Test
    void registerAsTutor_throwsException_whenUserNotFound() {
        // Arrange
        when(userRepo.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> tutorService.registerAsTutor(registerRequest, "nobody@test.com"));
        System.out.println("✅ TEST PASSED: Exception for non-existent user");
    }

    @Test
    void registerAsTutor_throwsException_whenDuplicateProfile() {
        // Arrange
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(studentUser));
        when(tutorProfileRepo.existsByUser(studentUser)).thenReturn(true); // already exists

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> tutorService.registerAsTutor(registerRequest, "alice@test.com"));
        System.out.println("✅ TEST PASSED: Duplicate tutor profile prevented");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STORY 2 TESTS: Check Eligibility (Strategy Pattern)
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void checkEligibility_returnsEligibleTrue_whenCriteriaMet() {
        // Arrange — strategy returns true
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(studentUser));
        when(tutorProfileRepo.findByUser(studentUser)).thenReturn(Optional.of(tutorProfile));
        when(eligibilityStrategy.isEligible(studentUser, tutorProfile)).thenReturn(true);
        when(eligibilityStrategy.getEligibilityCriteria()).thenReturn("GPA >= 3.0 AND points >= 50");

        // Act
        EligibilityResponse response = tutorService.checkEligibility("alice@test.com");

        // Assert
        assertTrue(response.isEligible());
        assertNotNull(response.getCriteria());
        assertTrue(response.getMessage().contains("eligible"));
        System.out.println("✅ TEST PASSED: Eligible user correctly identified");
    }

    @Test
    void checkEligibility_returnsEligibleFalse_whenCriteriaNotMet() {
        // Arrange — strategy returns false (GPA too low)
        studentUser.setPoints(10); // below threshold
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(studentUser));
        when(tutorProfileRepo.findByUser(studentUser)).thenReturn(Optional.of(tutorProfile));
        when(eligibilityStrategy.isEligible(studentUser, tutorProfile)).thenReturn(false);
        when(eligibilityStrategy.getEligibilityCriteria()).thenReturn("GPA >= 3.0 AND points >= 50");

        // Act
        EligibilityResponse response = tutorService.checkEligibility("alice@test.com");

        // Assert
        assertFalse(response.isEligible());
        System.out.println("✅ TEST PASSED: Ineligible user correctly identified");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STORY 3 TESTS: Create Session (Factory + Observer Pattern)
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void createSession_success_tutorCreatesSession() {
        // Arrange
        when(userRepo.findByEmail("bob@test.com")).thenReturn(Optional.of(tutorUser));
        when(sessionFactory.createSession(any(), any(), any(), anyInt(), any(), any()))
                .thenReturn(session);
        when(sessionRepo.save(any(TutoringSession.class))).thenReturn(session);

        // Act
        SessionResponse response = tutorService.createSession(sessionRequest, "bob@test.com");

        // Assert
        assertNotNull(response);
        assertEquals("Binary Trees", response.getTopic());
        assertEquals("DATA_STRUCTURES", response.getSubject());
        assertEquals("UPCOMING", response.getStatus());
        assertEquals("Bob", response.getTutorName());
        // Verify factory was used (not direct construction)
        verify(sessionFactory, times(1)).createSession(any(), any(), any(), anyInt(), any(), any());
        // Verify observer was fired
        verify(eventPublisher, times(1)).publishSessionCreated(any());
        System.out.println("✅ TEST PASSED: Session created via Factory, Observer fired");
    }

    @Test
    void createSession_throwsException_whenNotTutor() {
        // Arrange — user is a STUDENT, not a TUTOR
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(studentUser));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> tutorService.createSession(sessionRequest, "alice@test.com"));
        // Factory and repo should never be called
        verify(sessionFactory, never()).createSession(any(), any(), any(), anyInt(), any(), any());
        System.out.println("✅ TEST PASSED: Non-tutor blocked from creating session");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STORY 5 TESTS: View Sessions
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void getMySessions_returnsOnlyTutorsSessions() {
        // Arrange
        when(userRepo.findByEmail("bob@test.com")).thenReturn(Optional.of(tutorUser));
        when(sessionRepo.findByTutorOrderByScheduledAtDesc(tutorUser))
                .thenReturn(List.of(session));

        // Act
        List<SessionResponse> sessions = tutorService.getMySessions("bob@test.com");

        // Assert
        assertEquals(1, sessions.size());
        assertEquals("Binary Trees", sessions.get(0).getTopic());
        System.out.println("✅ TEST PASSED: Tutor sees only their own sessions");
    }

    @Test
    void getAllSessions_returnsAllSessions() {
        // Arrange
        when(sessionRepo.findAllByOrderByScheduledAtAsc()).thenReturn(List.of(session));

        // Act
        List<SessionResponse> sessions = tutorService.getAllSessions();

        // Assert
        assertEquals(1, sessions.size());
        System.out.println("✅ TEST PASSED: All sessions returned for browsing");
    }

    @Test
    void updateSession_throwsException_whenNotOwner() {
        // Arrange — different tutor trying to edit someone else's session
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(studentUser));
        when(sessionRepo.findById(1L)).thenReturn(Optional.of(session));
        // session.tutor is "bob@test.com", but alice is trying to update it

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> tutorService.updateSession(1L, sessionRequest, "alice@test.com"));
        System.out.println("✅ TEST PASSED: Unauthorized update blocked");
    }
}
