package highspeed;


	import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.apache.hadoop.io.DoubleWritable;


	public class Highspeed {
		public static class MapClass extends Mapper<LongWritable, Text, Text,LongWritable>{
			public void map(LongWritable key, Text value, Context context){
			try
				{
					String[] str=value.toString().split(",");
					long speed=Long.parseLong(str[1]);
					context.write(new Text(str[0]),new LongWritable(speed));
				}
			catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
		public static class ReduceClass extends Reducer<Text, LongWritable,NullWritable, Text>
		{
			//private LongWritable result =new LongWritable();

			
			public void reduce (Text Key ,Iterable<LongWritable> values,Context context) throws IOException, InterruptedException{
			long currentspeed =0;
			long max =65;
			long count=0;
			//double offence=0;
			long incoming=0;
			for (LongWritable val :values)
				{
				incoming++; 
					currentspeed=val.get();
					
						if(currentspeed>max)
						{
							//max=currentspeed;
							count++;
							System.out.println("");
							
						
							
						}
						
						
					
				}
			
			String output="Vechicle Number is:  "+Key+"\n "+ " Number of times entered "+incoming+" \n" +
					"And exceeded the limit "+count+"times and offence percentage is "+(count*100)/incoming+" %";
			context.write(null, new Text(output));
		/*	offence=(count/incoming)*100;
			context.write(Key,new LongWritable(count));
			context.write(Key,new LongWritable(offence));
			//per=(/sum)*100;
			//result.set(sum);
*/			
			
			}
		}
		
		public static void  main (String[] args) throws Exception {
		    Configuration conf = new Configuration();
		    Job job = Job.getInstance(conf, "volume count");
		    job.setJarByClass(Highspeed.class);
		    job.setMapperClass(MapClass.class);
		    job.setReducerClass(ReduceClass.class);
		    job.setOutputKeyClass(Text.class);
		    job.setOutputValueClass(LongWritable.class);
		    FileInputFormat.addInputPath(job, new Path(args[0]));
		    FileOutputFormat.setOutputPath(job, new Path(args[1]));
		    System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

	}



	


