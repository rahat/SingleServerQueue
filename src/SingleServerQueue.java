import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public class SingleServerQueue {

	private static final int numCustomers = 100000;

	private static double arrivalTime;

	private static double totalIdleTime;

	private static double totalServiceTime;

	private static double totalSystemTime;
	
	private static double simulationTime;

	private static boolean isBusy;

	private static ArrayList<Double> systemTimes = new ArrayList<Double>();

	private static ArrayList<Double> idleTimes = new ArrayList<Double>();

	private static ArrayList<Double> interarrivalTimes = new ArrayList<Double>();

	private static ArrayList<Double> serviceTimes = new ArrayList<Double>();

	private static DecimalFormat df = new DecimalFormat("###0.00");

	public static void executeSimulation() {

		double serviceTime = 0.0;

		double interarrivalTime = 0.0;

		double startTime = 0.0;

		double departureTime = 0.0;

		double idleTime = 0.0;

		double delay = 0.0;

		double systemTime = 0.0;

		Queue<Integer> waitingCustomers = new LinkedList<>();

		HashMap<Integer, Double> departureTimes = new HashMap<Integer, Double>();

		for (int customer = 1; customer <= numCustomers; customer++) {
			interarrivalTime = ThreadLocalRandom.current().nextDouble(0, 8); // 0 to 8 minutes
			interarrivalTimes.add(interarrivalTime);
			arrivalTime += interarrivalTime;

			if (arrivalTime < departureTime) {
				delay = departureTime - arrivalTime;
				isBusy = true;
				idleTime = 0.0;
			} else {
				isBusy = false;
				delay = 0.0;
			}

			// Server is idle
			if (!isBusy) {
				idleTime = (arrivalTime - simulationTime);
				idleTimes.add(idleTime);
				totalIdleTime += idleTime;
			}

			serviceTime = ThreadLocalRandom.current().nextDouble(0, 5); // 0 to 5 minutes
			serviceTimes.add(serviceTime);
			totalServiceTime += serviceTime;

			startTime = arrivalTime + delay;
			systemTime = serviceTime + delay;
			totalSystemTime += systemTime;
			systemTimes.add(systemTime);
			
			departureTime = arrivalTime + systemTime;
			departureTimes.put(customer, departureTime);

			simulationTime = arrivalTime;

			// Remove any customers that are already done with the service
			for (Entry<Integer, Double> entry : departureTimes.entrySet()) {
				// Start Time is Greater than Departure Time
				if (startTime > entry.getValue()) {
					waitingCustomers.remove(entry.getKey());
				}
			}

			// Server Currently Busy, Enqueue Customer
			if (isBusy) {
				waitingCustomers.add(customer); // Add to Waiting Customers Queue
			}

			System.out.println(customer + "\t\t " + df.format(arrivalTime) + "\t\t " + df.format(interarrivalTime)
					+ "\t\t " + df.format(serviceTime) + "\t\t " + df.format(startTime) + "\t\t "
					+ df.format(departureTime) + "\t\t " + df.format(delay) + "\t\t " + df.format(systemTime)
					+ "\t\t " + df.format(idleTime));

			System.out.println("Queue: " + waitingCustomers);

		}
	}

	public static double calcVariance(ArrayList<Double> values, double mean) {
		double variance = 0;
		for (int i = 0; i < values.size(); i++)
			variance += (values.get(i) - mean) * (values.get(i) - mean);
		return (double) variance / values.size();
	}

	public static void main(String[] args) {

		System.out.println(
				"Customer\t Arrival Time\t Interarrival\t Service Time\t Service Start\t Service End\t Wait Time\t System Time\t Idle Time");
		executeSimulation();

		System.out.println("Avg. Interarrival Time: " + df.format(arrivalTime / numCustomers));
		System.out.println("Variance of Avg. Interarrival Time: "
				+ df.format(calcVariance(interarrivalTimes, arrivalTime / numCustomers)));
		System.out.println("Avg. Service Time: " + df.format(totalServiceTime / numCustomers));
		System.out.println("Variance of Avg. Service Time: "
				+ df.format(calcVariance(serviceTimes, totalServiceTime / numCustomers)));
		System.out.println("Avg. Time That The Customer is in System: " + df.format(totalSystemTime / numCustomers));
		System.out.println("Variance of Avg. Time That The Customer is in System: "
				+ df.format(calcVariance(systemTimes, totalSystemTime / numCustomers)));
		System.out.println("Avg. Time That The Server Is Idle: " + df.format(totalIdleTime / numCustomers));
		System.out.println("Variance of Avg. Time That The Server Is Idle: "
				+ df.format(calcVariance(idleTimes, totalIdleTime / numCustomers)));

	}

}
