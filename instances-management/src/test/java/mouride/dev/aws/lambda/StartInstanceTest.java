package mouride.dev.aws.lambda;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class StartInstanceTest {

    @Mock
    private AmazonEC2 ec2;

    @InjectMocks
    private StartInstance startInstance;

    @BeforeEach
    void setUp() {
        String instanceId = "i-1234567890abcdef0";
        startInstance = new StartInstance(ec2, instanceId);
    }

    @Test
    void testHandleRequest() {
        // Given
        Map<String, String> event = new HashMap<>();
        event.put("InstanceId", "i-1234567890abcdef0");
        Context context = mock(Context.class);

        // Act
        String response = startInstance.handleRequest(event, context);

        // Then
        verify(ec2, times(1)).startInstances(any(StartInstancesRequest.class));
        assertEquals(String.format("Instance %s started", event.get("InstanceId")), response);
    }
}
