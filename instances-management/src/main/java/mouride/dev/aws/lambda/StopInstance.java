package mouride.dev.aws.lambda;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

/**
 * This class represents a Lambda function to stop an EC2 instance.
 * It can be initialized with an existing AmazonEC2 client or with default client and instanceId from environment variables.
 */
public class StopInstance implements RequestHandler<Map<String, String>, String> {

    /**
     * The AmazonEC2 client used to interact with EC2 service.
     */
    private final AmazonEC2 ec2;


    /**
     * The ID of the EC2 instance to stop.
     */
    private final String instanceId;

    /**
     * Constructor to initialize with an existing AmazonEC2 client and instanceId.
     *
     * @param ec2       The AmazonEC2 client.
     * @param instanceId The ID of the EC2 instance to stop.
     */
    public StopInstance(AmazonEC2 ec2, String instanceId) {
        this.ec2 = ec2;
        this.instanceId = instanceId;
    }

    /**
     * Constructor to initialize with default AmazonEC2 client and instanceId from environment variables.
     */
    public StopInstance() {
        this.ec2 = AmazonEC2ClientBuilder.defaultClient();
        this.instanceId = System.getenv("INSTANCE_ID");
    }

    /**
     * The handleRequest method is the entry point for the Lambda function.
     * It stops the specified EC2 instance and returns a confirmation message.
     *
     * @param input   The input to the Lambda function. In this case, it's a map of string to string.
     * @param context The context of the Lambda function.
     * @return A confirmation message indicating that the instance has been stopped.
     */
    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
        ec2.stopInstances(request);
        return String.format("Instance %s stopped", instanceId);
    }
}
