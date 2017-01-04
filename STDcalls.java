package stdcals;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class STDcalls {
    
    public static class MapClass extends Mapper <LongWritable,Text,LongWritable,LongWritable>{
        
        public void map(LongWritable Key,Text value,Context context)
        {
            try
            {
                String line=value.toString();
                String[] lineParts=line.split(",");
                if(lineParts[4].equals(1))
                {
                    long callid=Long.parseLong(lineParts[0]);
                    String starttime=lineParts[2];
                    String endtime=lineParts[3];
                    long duration=toMillis(starttime)-toMillis(endtime);
                    long duratioinminutes=(int)duration/(1000*60);
                    
                    context.write(new LongWritable(callid), new LongWritable(duratioinminutes));
                    
                    
                }
                
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        
        private long toMillis(String date) {
            
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date dateFrm = null;
            try {
                dateFrm = format.parse(date);

            } catch (ParseException e) {

                e.printStackTrace();
           }
            return dateFrm.getTime();
        }
    }
    
    
    public static class ReduceClass extends Reducer<LongWritable,LongWritable,Text,LongWritable>
    {
        static long sum=0;
        static long total=0;
        public void reduce(Text Key,Iterable<LongWritable>values,Context context)throws IOException, InterruptedException 
        { 
            for(LongWritable val:values)
            {
                sum+=val.get();
                
            }
            if(sum>=60)
            {
                context.write(Key, new LongWritable(sum));
            }
            
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapred.textoutputformat.separator",",");
        Job job = Job.getInstance(conf, "Std Calls");
        job.setJarByClass(STDcalls.class);
        job.setMapperClass(MapClass.class);
        job.setReducerClass(ReduceClass.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}