package local.to.popcornmovies.ui.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import local.to.popcornmovies.databinding.QualityListItemBinding;
import local.to.popcornmovies.models.QualityParsedModel;

public class QualitySpinnerAdapter extends ArrayAdapter<QualityParsedModel> {
    private final List<QualityParsedModel> qualityList;
//    private final OnQualitySelectedListener listener;

    public interface OnQualitySelectedListener {
        void onQualitySelected(QualityParsedModel qualityParsedModel);
    }

    public QualitySpinnerAdapter(@NonNull Context context, @NonNull List<QualityParsedModel> qualityList/*, @NonNull OnQualitySelectedListener listener*/) {
        super(context, 0, qualityList);
        this.qualityList = qualityList;
//        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createSelectedQualityView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createDropdownItemView(position, convertView, parent);
    }

    private View createSelectedQualityView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        QualityListItemBinding binding;

        if (convertView == null) {
            binding = QualityListItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (QualityListItemBinding) convertView.getTag();
        }

        QualityParsedModel model = qualityList.get(position);
        binding.qualtiyText.setText(model.quality);

        return convertView;
    }

    private View createDropdownItemView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        QualityListItemBinding binding;

        if (convertView == null) {
            binding = QualityListItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (QualityListItemBinding) convertView.getTag();
        }

        QualityParsedModel model = qualityList.get(position);
        binding.qualtiyText.setText(model.quality);

        /*convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQualitySelected(model);
            }
        });*/

        return convertView;
    }
}

