package mouride.dev.aws.lambda;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartInstanceTest {

    @Mock
    private AmazonEC2 ec2;

    @Mock
    private LambdaLogger logger;

    @InjectMocks
    private StartInstance startInstance;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        System.setProperty("INSTANCE_ID", "i-0a4779856f887e215");
        startInstance = new StartInstance(ec2, objectMapper, System.getProperty("INSTANCE_ID"));
    }

    @Test
    void testHandleRequestStartSuccess() {
        // Given
        // Create test event
        final Map<String, Object> detail = new HashMap<>();
        detail.put("action", "start");
        final Map<String, Object> event = new HashMap<>();
        event.put("detail", detail);

        final Context context = mock(Context.class);

        // Act
        final String response = startInstance.handleRequest(event, context);

        // Capture the request sent to AWS EC2 client
        final ArgumentCaptor<StartInstancesRequest> requestCaptor = ArgumentCaptor.forClass(StartInstancesRequest.class);
        verify(ec2, times(1)).startInstances(requestCaptor.capture());
        final StartInstancesRequest startRequest = requestCaptor.getValue();

        // Assert the request details
        assertEquals("i-0a4779856f887e215", startRequest.getInstanceIds().get(0));
        assertEquals("Instance i-0a4779856f887e215 started successfully", response);
    }

    @Test
    void testHandleRequest_UnknownAction() {
        // Create test event
        final Map<String, Object> detail = new HashMap<>();
        detail.put("action", "unknown");
        final Map<String, Object> event = new HashMap<>();
        event.put("detail", detail);
        final Context context = mock(Context.class);

        // Call the handler
        final String result = startInstance.handleRequest(event, context);

        // Assert the response
        assertEquals(String.format("Received event for action '%s', but only start action is supported", detail.get("action")),
                result);

        // Verify no interaction with EC2 client
        verifyNoInteractions(ec2);
    }

    @Test
    void testHandleRequestErrorStartingInstance() {
        // Create test event
        final Map<String, Object> detail = new HashMap<>();
        final Map<String, Object> event = new HashMap<>();
        event.put("detail", detail);
        final Context context = mock(Context.class);

        // When
        when(context.getLogger()).thenReturn(logger);

        // Call the handler
        final String result = startInstance.handleRequest(event, context);

        // Assert the response
        assertEquals(String.format("Failed to start instance %s", System.getProperty("INSTANCE_ID")), result);

        // Verify no interaction with EC2 client
        verifyNoInteractions(ec2);
    }
}
