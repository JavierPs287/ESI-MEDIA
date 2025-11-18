import { Injectable } from '@angular/core';
import { FormGroup, AbstractControl, ValidationErrors } from '@angular/forms';

@Injectable({
    providedIn: 'root'
})
export class ContentFormService {
    minTagsValidator(min: number) {
        return (control: AbstractControl): ValidationErrors | null => {
            const tags = control.value;
            return tags && tags.length >= min ? null : { minTags: true };
        };
    }

    durationValidator() {
        return (group: AbstractControl): ValidationErrors | null => {
            const hours = group.get('hours')?.value || 0;
            const minutes = group.get('minutes')?.value || 0;
            const seconds = group.get('seconds')?.value || 0;
            const totalSeconds = hours * 3600 + minutes * 60 + seconds;
            return totalSeconds > 0 ? null : { invalidDuration: true };
        };
    }

    urlValidator() {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) {
                return null;
            }
            try {
                new URL(control.value);
                return null;
            } catch {
                return { invalidUrl: true };
            }
        };
    }

    convertDurationToSeconds(duration: any): number {
        const hours = duration.hours || 0;
        const minutes = duration.minutes || 0;
        const seconds = duration.seconds || 0;
        return (hours * 3600) + (minutes * 60) + seconds;
    }

    getImageIdFromUrl(imageUrl: string | null, availableImages: any[]): number {
        if (!imageUrl) return 0;
        const imageOption = availableImages.find(img => img.url === imageUrl);
        return imageOption ? Number.parseInt(imageOption.name, 10) : 0;
    }

    appendCommonFields(
        formData: FormData,
        form: FormGroup,
        selectedImage: string | null,
        availableImages: any[]
    ): void {
        formData.append('title', form.value.title);
        formData.append('duration', this.convertDurationToSeconds(form.value.duration).toString());
        formData.append('vip', form.value.vip.toString());
        formData.append('visible', form.value.visible.toString());
        formData.append('minAge', form.value.ageRestriction.toString());

        if (form.value.description) {
            formData.append('description', form.value.description);
        }

        if (form.value.tags && form.value.tags.length > 0) {
            for (const tag of form.value.tags) {
                formData.append('tags', tag);
            }
        }

        if (form.value.availableUntil) {
            const dateValue = typeof form.value.availableUntil === 'string'
                ? new Date(form.value.availableUntil)
                : form.value.availableUntil;
            formData.append('visibilityDeadline', dateValue.toISOString());
        }

        const imageId = this.getImageIdFromUrl(selectedImage, availableImages);
        formData.append('imageId', imageId.toString());
    }

    markFormGroupTouched(formGroup: FormGroup): void {
        for (const key in formGroup.controls) {
            const control = formGroup.get(key);
            control?.markAsTouched();
            if (control instanceof FormGroup) {
                this.markFormGroupTouched(control);
            }
        }
    }
}