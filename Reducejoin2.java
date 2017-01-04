package reducejoin2;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Reducejoin2 {

	public static class ProductionMapper extends
			Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String record = value.toString();
			String[] parts = record.split(",");
			
			context.write(new Text(parts[0]), new Text("production\t" +parts[1]));
		}
	}

	public static class SalesMapper extends
			Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String record = value.toString();
			String[] parts = record.split(",");
			context.write(new Text(parts[0]), new Text("sales\t" + parts[1]));
		}
	}

	public static class ReduceJoinReducer extends
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
		
			String sales = "";
			String production="";
			
			for (Text t : values) {
				String parts[] = t.toString().split("\t");
				if (parts[0].equals("sales.txt")) {
					sales += Float.parseFloat(parts[1]);
				} else if (parts[0].equals("production.txt")) {
					production = parts[1];
				}
			}
			
			context.write(new Text(production), new Text(sales));
		}
	}

	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
	    job.setJarByClass(Reducejoin2.class);
	    job.setJobName("Reduce Side Join");
		job.setReducerClass(ReduceJoinReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		MultipleInputs.addInputPath(job, new Path(args[0]),TextInputFormat.class, ProductionMapper.class);
		MultipleInputs.addInputPath(job, new Path(args[1]),TextInputFormat.class, SalesMapper.class);
		
		Path outputPath = new Path(args[2]);
		FileOutputFormat.setOutputPath(job, outputPath);
		//outputPath.getFileSystem(conf).delete(outputPath);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}