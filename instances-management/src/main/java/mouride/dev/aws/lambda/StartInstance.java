package mouride.dev.aws.lambda;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

/**
 * This class represents a Lambda function to start an EC2 instance.
 * It uses the AWS SDK for Java (v2) to interact with the EC2 service.
 */
public class StartInstance implements RequestHandler<Map<String, String>, String> {

    private final AmazonEC2 ec2;

    private final String instanceId;

    /**
     * Default constructor.
     * It initializes the AmazonEC2 client using the default credentials provider chain.
     * It retrieves the instance ID from the environment variable "INSTANCE_ID".
     */
    public StartInstance() {
        this.ec2 = AmazonEC2ClientBuilder.defaultClient();
        this.instanceId = System.getenv("INSTANCE_ID");
    }

    /**
     * Constructor for dependency injection (useful for testing).
     *
     * @param ec2       The AmazonEC2 client to use for making API calls.
     * @param instanceId The ID of the EC2 instance to start.
     */
    public StartInstance(AmazonEC2 ec2, String instanceId) {
        this.ec2 = ec2;
        this.instanceId = instanceId;
    }

    /**
     * This method is called when the Lambda function is triggered.
     * It starts the specified EC2 instance and returns a confirmation message.
     *
     * @param input   The input to the Lambda function. In this case, it is a map of string to string.
     * @param context The context in which the Lambda function is running.
     * @return A confirmation message indicating that the instance has been started.
     */
    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instanceId);
        ec2.startInstances(request);
        return String.format("Instance %s started", instanceId);
    }

}
