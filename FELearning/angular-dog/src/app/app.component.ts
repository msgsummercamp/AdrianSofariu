import { HttpClient } from '@angular/common/http';
import { Component, inject, signal, WritableSignal } from '@angular/core';
import { DogApiResponse } from '../types/dog-api-response';
import { MatToolbar } from '@angular/material/toolbar';

@Component({
  selector: 'app-root',
  imports: [MatToolbar],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  public readonly title = 'angular-dog';

  private readonly httpClient: HttpClient = inject(HttpClient);
  private readonly dogUrl: string = 'https://dog.ceo/api/breeds/image/random';

  public readonly imageUrl: WritableSignal<string> = signal('');
  public readonly errorMessage: WritableSignal<string> = signal('');
  public readonly loading: WritableSignal<boolean> = signal(false);

  public loadDogImage(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.httpClient.get<DogApiResponse>(this.dogUrl).subscribe({
      next: (response: DogApiResponse) => {
        if (response.status === 'success') {
          this.imageUrl.set(response.message);
          this.errorMessage.set('');
        } else {
          this.imageUrl.set('');
          this.errorMessage.set('Failed to display image.');
        }
        this.loading.set(false);
      },
      error: (error: Error) => {
        this.imageUrl.set('');
        this.errorMessage.set('Failed to load image - ' + error.message);
        this.loading.set(false);
      },
    });
  }
}
