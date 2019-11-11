package ngems.c2f;

public interface PositionReportMessageDataReaderOperations extends
    DDS.DataReaderOperations
{

    int read(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_w_condition(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            DDS.ReadCondition a_condition);

    int take_w_condition(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            DDS.ReadCondition a_condition);

    int read_next_sample(
            ngems.c2f.PositionReportMessageHolder received_data, 
            DDS.SampleInfoHolder sample_info);

    int take_next_sample(
            ngems.c2f.PositionReportMessageHolder received_data, 
            DDS.SampleInfoHolder sample_info);

    int read_instance(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples,
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take_instance(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_next_instance(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int take_next_instance(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            int sample_states, 
            int view_states, 
            int instance_states);

    int read_next_instance_w_condition(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            DDS.ReadCondition a_condition);

    int take_next_instance_w_condition(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq, 
            int max_samples, 
            long a_handle, 
            DDS.ReadCondition a_condition);

    int return_loan(
            ngems.c2f.PositionReportMessageSeqHolder received_data, 
            DDS.SampleInfoSeqHolder info_seq);

    int get_key_value(
            ngems.c2f.PositionReportMessageHolder key_holder, 
            long handle);
    
    long lookup_instance(
            ngems.c2f.PositionReportMessage instance);

}
