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
import local.to.popcornmovies.models.AnimeStreamingSource;

public class SourceSpinnerAdapter extends ArrayAdapter<AnimeStreamingSource> {
    private final List<AnimeStreamingSource> qualityList;

    public SourceSpinnerAdapter(@NonNull Context context, @NonNull List<AnimeStreamingSource> qualityList/*, @NonNull OnQualitySelectedListener listener*/) {
        super(context, 0, qualityList);
        this.qualityList = qualityList;
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

        AnimeStreamingSource model = qualityList.get(position);
        binding.qualtiyText.setText(model.sourceName);

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

        AnimeStreamingSource model = qualityList.get(position);
        binding.qualtiyText.setText(model.sourceName);

        return convertView;
    }
}

