package ngems.c2f;

public interface PointOfInterestMessageDataReaderViewOperations extends
    DDS.DataReaderViewOperations
{

    int read(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_w_condition(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            DDS.ReadCondition a_condition);

    int take_w_condition(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            DDS.ReadCondition a_condition);

    int read_next_sample(
            ngems.c2f.PointOfInterestMessageHolder received_data, 
            DDS.SampleInfoHolder sample_info);

    int take_next_sample(
            ngems.c2f.PointOfInterestMessageHolder received_data, 
            DDS.SampleInfoHolder sample_info);

    int read_instance(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples,
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take_instance(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_next_instance(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take_next_instance(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_next_instance_w_condition(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            DDS.ReadCondition a_condition);

    int take_next_instance_w_condition(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            DDS.ReadCondition a_condition);

    int return_loan(
            ngems.c2f.PointOfInterestMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq);

    int get_key_value(
            ngems.c2f.PointOfInterestMessageHolder key_holder, 
            long handle);
    
    long lookup_instance(
            ngems.c2f.PointOfInterestMessage instance);

}
