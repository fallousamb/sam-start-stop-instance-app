package mouride.dev.aws.lambda;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class StopInstance implements RequestHandler<Map<String, String>, String> {
    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        final String instanceId = System.getenv("INSTANCE_ID");
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
        ec2.stopInstances(request);
        return "Instance stopped";
    }
}
