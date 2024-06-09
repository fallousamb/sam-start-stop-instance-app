package mouride.dev.aws.lambda;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
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
class StopInstanceTest {

    @Mock
    private AmazonEC2 ec2;

    @InjectMocks
    private StopInstance stopInstance;

    @BeforeEach
    void setUp() {
        String instanceId = "i-1234567890abcdef0";
        stopInstance = new StopInstance(ec2, instanceId);
    }

    @Test
    void testStopInstance() {
        // Given
        Map<String, String> event = new HashMap<>();
        event.put("InstanceId", "i-1234567890abcdef0");
        Context context = mock(Context.class);
        String response = stopInstance.handleRequest(event, context);

        // Then
        verify(ec2, times(1)).stopInstances(any(StopInstancesRequest.class));
        assertEquals(String.format("Instance %s stopped", event.get("InstanceId")), response);
    }
}
