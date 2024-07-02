package mouride.dev.aws.lambda;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StopInstanceTest {

    @Mock
    private AmazonEC2 ec2;

    @Mock
    private LambdaLogger logger;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private StopInstance stopInstance;

    @BeforeEach
    void setUp() {
        System.setProperty("INSTANCE_ID", "i-0a4779856f887e215");
        stopInstance = new StopInstance(ec2, objectMapper, System.getProperty("INSTANCE_ID"));
    }

    @Test
    void testStopInstanceSuccess() {
        // Given
        // Create test event
        final Map<String, Object> detail = new HashMap<>();
        detail.put("action", "stop");
        final Map<String, Object> event = new HashMap<>();
        event.put("detail", detail);

        final Context context = mock(Context.class);
        final String response = stopInstance.handleRequest(event, context);

        // Capture the request sent to AWS EC2 client
        final ArgumentCaptor<StopInstancesRequest> requestCaptor = ArgumentCaptor.forClass(StopInstancesRequest.class);
        verify(ec2, times(1)).stopInstances(requestCaptor.capture());
        final StopInstancesRequest startRequest = requestCaptor.getValue();

        // Then
        verify(ec2, times(1)).stopInstances(any(StopInstancesRequest.class));// Assert the request details
        assertEquals("i-0a4779856f887e215", startRequest.getInstanceIds().get(0));
        assertEquals("Instance i-0a4779856f887e215 stopped successfully", response);
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
        final String result = stopInstance.handleRequest(event, context);

        // Assert the response
        assertEquals(String.format("Received event for action '%s', but only stop action is supported", detail.get("action")),
                result);

        // Verify no interaction with EC2 client
        verifyNoInteractions(ec2);
    }

    @Test
    void testHandleRequestErrorStopingingInstance() {
        // Create test event
        final Map<String, Object> detail = new HashMap<>();
        final Map<String, Object> event = new HashMap<>();
        event.put("detail", detail);
        final Context context = mock(Context.class);

        // When
        when(context.getLogger()).thenReturn(logger);

        // Call the handler
        final String result = stopInstance.handleRequest(event, context);

        // Assert the response
        assertEquals(String.format("Failed to stop instance %s", System.getProperty("INSTANCE_ID")), result);

        // Verify no interaction with EC2 client
        verifyNoInteractions(ec2);
    }
}
