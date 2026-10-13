import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { LabelService } from './label';

describe('LabelService', () => {
  let service: LabelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(LabelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
