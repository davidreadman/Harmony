package ngems.c2f;

public interface HeartbeatMessageDataWriterOperations extends
    DDS.DataWriterOperations
{

    long register_instance(
            ngems.c2f.HeartbeatMessage instance_data);

    long register_instance_w_timestamp(
            ngems.c2f.HeartbeatMessage instance_data, 
            DDS.Time_t source_timestamp);

    int unregister_instance(
            ngems.c2f.HeartbeatMessage instance_data, 
            long handle);

    int unregister_instance_w_timestamp(
            ngems.c2f.HeartbeatMessage instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int write(
            ngems.c2f.HeartbeatMessage instance_data, 
            long handle);

    int write_w_timestamp(
            ngems.c2f.HeartbeatMessage instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int dispose(
            ngems.c2f.HeartbeatMessage instance_data, 
            long instance_handle);

    int dispose_w_timestamp(
            ngems.c2f.HeartbeatMessage instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);
    
    int writedispose(
            ngems.c2f.HeartbeatMessage instance_data, 
            long instance_handle);

    int writedispose_w_timestamp(
            ngems.c2f.HeartbeatMessage instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);

    int get_key_value(
            ngems.c2f.HeartbeatMessageHolder key_holder, 
            long handle);
    
    long lookup_instance(
            ngems.c2f.HeartbeatMessage instance_data);

}
